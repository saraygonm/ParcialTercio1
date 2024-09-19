package edu.eci.arsw.math;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

///  <summary>
///  An implementation of the Bailey-Borwein-Plouffe formula for calculating hexadecimal
///  digits of pi.
///  https://en.wikipedia.org/wiki/Bailey%E2%80%93Borwein%E2%80%93Plouffe_formula
///  *** Translated from C# code: https://github.com/mmoroney/DigitsOfPi ***
///  </summary>
public class PiDigits {
    //impresion según el usuario
    public static Scanner sca = new Scanner(System.in);


    // private static int DigitsPerSum = 8;
    //private static double Epsilon = 1e-17;

    
    /**
     * Returns a range of hexadecimal digits of pi.
     * @param start The starting location of the range.
     * @param count The number of digits to return
     * @return An array containing the hexadecimal digits.
     */
    public static byte[] getDigits(int start, int count, int N) {
        byte [] digits = new byte[count];
        int digitsPerThread = count/ N;
        boolean ThreadRun = true;
        Object lockObject = new Object();
        List<PiDigitsThread> threads = new ArrayList<>();



        verificarIntervalo(start, count);
        creaThreads(start, N, threads, digitsPerThread, lockObject);

        //se usa para detener en caso de que un thear este ejecutandose
        try {
            while (ThreadRun) {
                ThreadRun = false;
                for (PiDigitsThread thread : threads) {
                    if (thread.isAlive()) {
                        ThreadRun= true;
                        break;
                    }
                }

                //Dormimos el tread
                Thread.sleep(5000);
                stopAndExec(threads, lockObject);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        getResults(threads, digits, digitsPerThread);
        getTotalDigits(threads);
        return digits;
    }

    /**
     * Detiene y ejecuta los threads
     */
    private static void stopAndExec(List<PiDigitsThread> threads, Object lockObject) throws InterruptedException {
        for (PiDigitsThread thread : threads) {
            thread.setExec(false);
        }
        System.out.println("Deteneiendo los threads: ");
        getTotalDigits(threads);
        System.out.println("Press ENTER para continuar....");
        System.out.println("------------------------------ ");
        sca.nextLine();
        System.out.println("Resumen Total de los threads (analizando....): ");
        for (PiDigitsThread thread : threads) {
            thread.setExec(true);
            synchronized (lockObject) {
                lockObject.notifyAll();
            }
        }
    }
    /**
     * Crea los threads
     */
    private static void creaThreads(int start, int N, List<PiDigitsThread> threads, int digitsPerThread, Object lockObject) {
        for (int i = 0; i < N; i++) {
            PiDigitsThread thread = new PiDigitsThread(start, digitsPerThread, i, lockObject);
            start += digitsPerThread; // Actualiza el inicio del intervalo
            threads.add(thread); // se añade el  uevo thread
            thread.start();
        }
    }

    /**
     * Obtener los digitos  Thread
     */

    private static void getThreadDigits(byte[] digits, int digitsPerThread, PiDigitsThread thread) {
        byte[] threadDigits = thread.CalculoDigits();
        //ciclo para validar  la longitud de los digitos
        for (int i = 0; i < threadDigits.length; i++) {
            // Actualizar digitos en arreglo
            digits[i + thread.getid() * digitsPerThread] = threadDigits[i];
        }
    }
    /**
     * determinacion si el intervalo  es valido
     */
    private static void verificarIntervalo( int start, int count){
        if (start < 0  ||  count < 0 ) { //tanto el start como el count
            throw new RuntimeException("Intervalo valido");
        }
    }

    /**
     * Imprimir la totalidad de los digitos procesados
     */
    private static void getTotalDigits(List<PiDigitsThread> threads) {
        int totalDigits = 0;
        for (PiDigitsThread thread : threads) {
            totalDigits += thread.procesos();
        }
        System.out.println("Total de digitos en el proceso: " + totalDigits);
    }

    /**
     * imprimir los Resultados de los  Threads
     */
    private static void getResults(List<PiDigitsThread> threads, byte[] digits, int digitsPerThread) {
        for (PiDigitsThread thread : threads) {
            try {
                thread.join();
                getThreadDigits(digits, digitsPerThread, thread);
            } catch (InterruptedException ex) {
                System.out.println("El Thread ha sido interrumpido");
            }
        }
    }



}




