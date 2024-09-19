package edu.eci.arsw.math;

public class PiDigitsThread extends Thread {

    private int start;
    private int count;
    private int Id;
    private byte[] digits;
    private static int DIGITSPERSUM = 8;
    private static double EPSILON = 1e-17;

    private boolean execution;
    private Object Obl;
    private int processedDigits;

    public PiDigitsThread(int start, int count, int name, Object lockObject) {
        this.start = start;
        this.count = count;
        this.Id = name;
        this.digits = new byte[count];
        this.execution = true;
        this.Obl = lockObject;
        this.processedDigits = 0;
    }




    /**
     * Retornar digitos que se calcularon (Aray)
     */
    public byte[] CalculoDigits(){
        return digits;
    }
    /**
     * Retorna el id del thread
     */
    public int getid(){
        return this.Id;
    }
    /**
     * Retornar la cantidad de digitos  que se procesaron
     */
    public int procesos(){
        return processedDigits;
    }



    /**
     * Estado de ejecucion
     */
    public void setExec(boolean execution) {
        this.execution = execution;
    }

    /**
     * retornar suma
     */
    private double sum(int m, int n) {
        double sum = 0;
        int a = m;
        int power = n;

        while (true) {
            double term;
            if (power > 0) {
                term = (double) hexExponentModulo(power, a) / a;
            } else {
                term = Math.pow(16, power) / a;
                if (term < EPSILON) {
                    break;
                }
            }
            sum += term;
            power--;
            a += 8;
        }
        return sum;
    }

    /**
     * Retornar 16^p mod m.
     */
    private int hexExponentModulo(int b, int m) {
        int power = 1;
        while (power * 2 <= b) {
            power *= 2;
        }
        int result = 1;
        while (power > 0) {
            if (b >= power) {
                result *= 16;
                result %= m;
                b -= power;
            }
            power /= 2;
            if (power > 0) {
                result *= result;
                result %= m;
            }
        }
        return result;
    }
    @Override
    public void run() {
        double sum = 0;
        for (int i = 0; i < count; i++) {
            processedDigits = i + 1;

            synchronized (Obl) { // revision para dternerse
                if (!execution) {
                    try {
                        Obl.wait(); //espeta
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Logica
            if (i % DIGITSPERSUM == 0) {
                sum = 4 * sum(1, start) - 2 * sum(4, start) - sum(5, start) - sum(6, start);
                start += DIGITSPERSUM;
            }

            sum = 16 * (sum - Math.floor(sum));
            digits[i] = (byte) sum;
        }
    }

}


