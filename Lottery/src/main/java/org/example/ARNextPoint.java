
package org.example;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import java.util.Arrays;
import java.util.List;

/**
 * Прогноза на следваща стойност от ред от измервания чрез AR(p) (авторегресия).
 * - Избор на p по AIC/BIC.
 * - Работи по оригинални стойности или по първи разлики (за трендове).
 * - Връща точка + (1-α) ПРОГНОЗЕН интервал.
 * <p>
 * Зависимост (Maven):
 * <dependency>
 * <groupId>org.apache.commons</groupId>
 * <artifactId>commons-math3</artifactId>
 * <version>3.6.1</version>
 * </dependency>
 */
public final class ARNextPoint {

    /**
     * Резултат от прогнозата.
     */
    public static final class Forecast {
        /**
         * Точкова прогноза за следващата стойност (в оригинален мащаб).
         */
        public final double yHat;
        /**
         * Долна/горна граница на (1-α) прогнозния интервал.
         */
        public final double lo, hi;
        /**
         * Избраният ред p на AR модела.
         */
        public final int p;
        /**
         * Флаг дали сме моделирали първи разлики.
         */
        public final boolean usedDiff;
        /**
         * Дали моделът включва интерсепт (свободен член).
         */
        public final boolean includeIntercept;

        public Forecast(double yHat, double lo, double hi, int p, boolean usedDiff, boolean includeIntercept) {
            this.yHat = yHat;
            this.lo = lo;
            this.hi = hi;
            this.p = p;
            this.usedDiff = usedDiff;
            this.includeIntercept = includeIntercept;
        }

        //        @Override public String toString() {
//            return String.format("ŷ_next=%.6f  [%.6f, %.6f]  (p=%d, diff=%s, intercept=%s)",
//                    yHat, lo, hi, p, usedDiff, includeIntercept);
//        }
        @Override
        public String toString() {
            return String.format("%.2f", yHat);
        }
    }

    /**
     * Главен метод за прогноза.
     *
     * @param y       ред от измервания (равномерно по време). Минимум ~50 е разумно, но работи и с по-малко.
     * @param pMax    максимален ред за търсене (напр. 20 при n≈200).
     * @param useDiff ако true: моделира първи разлики (помага при тренд/нестационарност).
     * @param alpha   ниво за интервала (0.05 → 95%).
     * @param useBIC  ако true: критерий за избор е BIC; иначе – AIC.
     */
    public static Forecast predictNext(double[] y, int pMax, boolean useDiff, double alpha, boolean useBIC) {
        if (y == null || y.length < 10)
            throw new IllegalArgumentException("Твърде кратък ред. Нужни са поне ~10-15 точки.");

        // 1) По избор – модел върху първи разлики z_t = y_t - y_{t-1}.
        //    При разлики често няма нужда от интерсепт (средната е ~0) → по-стабилна оценка.
        final double[] series = useDiff ? diff(y) : y;
        final boolean includeIntercept = !useDiff;

        final int n = series.length;
        final int maxP = Math.min(pMax, Math.min(20, n - 3)); // предпазна горна граница

        int bestP = -1;
        double bestCrit = Double.POSITIVE_INFINITY;

        // 2) Търсене на p по AIC/BIC (прескачаме сингулярни конфигурации)
        for (int p = 1; p <= maxP; p++) {
            Design d = design(series, p);          // X: само лагове (БЕЗ константа); y: изместени напред
            int nFit = d.y.length;
            int k = p + (includeIntercept ? 1 : 0); // брой параметри в модела

            if (nFit <= k) continue;               // недостатъчно редове → ще е сингулярно

            OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();
            // Важно: когато includeIntercept=true, НЕ добавям колона от единици в X;
            // OLSMultipleLinearRegression ще добави интерсепт автоматично, ако setNoIntercept(false).
            ols.setNoIntercept(!includeIntercept);

            try {
                ols.newSampleData(d.y, d.X);
                double rss = ols.calculateResidualSumOfSquares();

                // Класически формули за регресия:
                // AIC = n*ln(RSS/n) + 2*k;   BIC = n*ln(RSS/n) + k*ln(n)
                double crit = useBIC
                        ? nFit * Math.log(rss / nFit) + k * Math.log(nFit)
                        : nFit * Math.log(rss / nFit) + 2.0 * k;

                if (crit < bestCrit) {
                    bestCrit = crit;
                    bestP = p;
                }
            } catch (SingularMatrixException ex) {
                // Линейна зависимост (напр. прекалено голям p или почти константни лагове) – прескачам този p.
                continue;
            }
        }

        if (bestP < 0)
            throw new IllegalStateException("Всички кандидати за p водят до сингулярна матрица. " +
                    "Намалете pMax, увеличете данните или моделирайте разлики (useDiff=true).");

        // 3) Финално напасване с избрания p (ако се наложи, намалявам p, докато стане несингулярно)
        int p = bestP;
        OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();
        ols.setNoIntercept(!includeIntercept);
        Design d = design(series, p);
        while (true) {
            try {
                ols.newSampleData(d.y, d.X);
                break; // успешно
            } catch (SingularMatrixException ex) {
                if (--p < 1) throw new IllegalStateException("Неуспешно напасване дори след намаляване на p.");
                d = design(series, p);
            }
        }

        double[] beta = ols.estimateRegressionParameters();                 // β (вкл. интерсепт ако includeIntercept=true)
        double[][] varBetaArr = ols.estimateRegressionParametersVariance(); // Var(β)
        RealMatrix varBeta = new Array2DRowRealMatrix(varBetaArr);
        double sigma2 = ols.estimateErrorVariance();                        // σ²
        int nFit = d.y.length;
        int k = beta.length;                                                // реален брой параметри (вкл. интерсепт ако има)
        int df = Math.max(1, nFit - k);                                     // степени на свобода

        // 4) Една стъпка напред: x0 = [ (1), y_t, y_{t-1}, ..., y_{t-p+1} ]
        double[] x0 = includeIntercept ? new double[p + 1] : new double[p];
        int idx = 0;
        if (includeIntercept) x0[idx++] = 1.0;                              // 1 само ако има интерсепт
        for (int j = 0; j < p; j++) x0[idx++] = series[n - 1 - j];

        double yHatCore = dot(beta, x0);                                    // прогноза за нивото/разликата
        // Ако съм работил върху разликите, добавям последната действителна стойност, за да се върна в оригинален мащаб.
        double yHat = useDiff ? (y[y.length - 1] + yHatCore) : yHatCore;

        // 5) ПРОГНОЗЕН интервал: se^2 = σ² * (1 + x0' Var(β) x0)
        RealVector xVec = new ArrayRealVector(x0);
        double se2 = sigma2 * (1.0 + xVec.dotProduct(varBeta.operate(xVec)));
        double t = new TDistribution(df).inverseCumulativeProbability(1.0 - alpha / 2.0);
        double half = t * Math.sqrt(Math.max(se2, 0.0));
        double lo = yHat - half, hi = yHat + half;

        return new Forecast(yHat, lo, hi, p, useDiff, includeIntercept);
    }

    // ---------- помощни методи (ясни и изолирани) ----------
    /**
     * Първа разлика z_t = y_t - y_{t-1}. Дължина: y.length-1.
     */
    private static double[] diff(double[] y) {
        double[] z = new double[y.length - 1];
        for (int i = 1; i < y.length; i++) z[i - 1] = y[i] - y[i - 1];
        return z;
    }

    /**
     * Скаларно произведение.
     */
    private static double dot(double[] a, double[] b) {
        double s = 0;
        for (int i = 0; i < a.length; i++) s += a[i] * b[i];
        return s;
    }

    /**
     * Дизайн-матрица за AR(p): X съдържа САМО лагове (без константа).
     */
    private static Design design(double[] series, int p) {
        int rows = series.length - p;
        double[] y = new double[rows];
        double[][] X = new double[rows][p];
        for (int r = 0; r < rows; r++) {
            y[r] = series[p + r];                  // текуща стойност
            for (int j = 0; j < p; j++) {
                X[r][j] = series[p + r - 1 - j];   // лагове: y_{t-1}, y_{t-2}, ...
            }
        }
        return new Design(y, X);
    }

    private static final class Design {
        final double[] y;
        final double[][] X;

        Design(double[] y, double[][] X) {
            this.y = y;
            this.X = X;
        }
    }

    public static double[] listToArray(List<Double> list) {
        return list.stream().mapToDouble(Double::doubleValue).toArray();
    }


    static void prediction(int lastNNumbers) {
        // 1) Чете данни
        List<Double> tempY = ParseURL.readArData("/Users/blagojnikolov/Desktop/@tmp/stdResults.txt");
        double[] y = listToArray(tempY);
        int n = y.length;

        // 2) Проверка, че ползвам точно тези стойности (последните 5 от файла)
        //System.out.println("N = " + n);

        // 3) Прогноза
        Forecast f1 = predictNext(y, /*pMax=*/20, /*useDiff=*/true,  /*alpha=*/0.05, /*useBIC=*/true);
        System.out.print(f1 + " ÷ ");
        Forecast f2 = predictNext(y, /*pMax=*/20, /*useDiff=*/false, /*alpha=*/0.05, /*useBIC=*/true);
        System.out.println(f2);;
        System.out.println("Последни наблюдавани: " +
                Arrays.toString(Arrays.copyOfRange(y, Math.max(0, lastNNumbers - 3), lastNNumbers)) + "\n");
    }

    // ------------------------ ДЕМО ------------------------
    public static void main(String[] args) throws Exception {
        // 1) Чети твоите данни
        List<Double> tempY = ParseURL.readArData("/Users/blagojnikolov/Desktop/@tmp/stdResults.txt");
        double[] y = listToArray(tempY);
        int n = y.length;

        // 2) Провери, че ползваме точно тези стойности (последните 5 от файла)
        System.out.println("N = " + n);
        System.out.println("Последни наблюдавани: " +
                Arrays.toString(Arrays.copyOfRange(y, Math.max(0, n - 3), n)));

        // 3) Прогноза
        Forecast f1 = predictNext(y, /*pMax=*/20, /*useDiff=*/true,  /*alpha=*/0.05, /*useBIC=*/true);
        System.out.println(f1);
        Forecast f2 = predictNext(y, /*pMax=*/20, /*useDiff=*/false, /*alpha=*/0.05, /*useBIC=*/true);
        System.out.println(f2);
    }

}



