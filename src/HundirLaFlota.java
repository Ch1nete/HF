import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Scanner;

public class HundirLaFlota {
    public static void main(String[] args) {

        Scanner entrada = new Scanner(System.in);
        int dimensionTablero = 11;
        int marcadorJugador = 0, marcadorPC = 0;


        char[][] tableroJugador = new char[dimensionTablero][dimensionTablero];
        char[][] tableroDisparosJugador = new char[dimensionTablero][dimensionTablero];
        char[][] tableroPC = new char[dimensionTablero][dimensionTablero];
        char[][] tableroDisparosPC = new char[dimensionTablero][dimensionTablero];
        int[] barcos = new int[]{4, 4, 3, 2, 1};
        boolean menu;

        System.out.println("                                                                                 ");
        System.out.println("  _   _ _   _ _   _ ____ ___ ____    _        _      _____ _     ___ _____  _    ");
        System.out.println(" | | | | | | | \\ | |  _ \\_ _|  _ \\  | |      / \\    |  ___| |   / _ \\_   _|/ \\   ");
        System.out.println(" | |_| | | | |  \\| | | | | || |_) | | |     / _ \\   | |_  | |  | | | || | / _ \\  ");
        System.out.println(" |  _  | |_| | |\\  | |_| | ||  _ <  | |___ / ___ \\  |  _| | |__| |_| || |/ ___ \\ ");
        System.out.println(" |_| |_|\\___/|_| \\_|____/___|_| \\_\\ |_____/_/   \\_\\ |_|   |_____\\___/ |_/_/   \\_\\");
        System.out.println();
        System.out.println("                                   Eric Orduña Suárez                                     ");
        System.out.println("\n Quieres jugar en modo Standard? O con trucos?: ");
        System.out.println();
        System.out.println(" 0 -> Standard");
        System.out.println(" 1 -> Chetos");

        if (entrada.nextInt() == 0) {
            menu = false;
        } else {
            menu = true;
        }

        System.out.println("Introduce el nombre del jugador: ");
        String nombre = entrada.next();

        borrarPantalla();

        inicializarTablero(tableroJugador);
        inicializarTablero(tableroDisparosJugador);
        visualizarTablero(tableroJugador, tableroDisparosJugador);
        colocarBarcos2(tableroJugador, tableroDisparosJugador, barcos);

        inicializarTablero(tableroPC);
        inicializarTablero(tableroDisparosPC);
        colocarBarcosPC(tableroPC, barcos);
        System.out.println("\n Tablero PC \t\t\t\t Tablero Disparos PC");
        visualizarTablero(tableroPC, tableroDisparosPC);

        marcadorJugador = sumaMarcador(barcos);
        marcadorPC = marcadorJugador;

        System.out.println("Marcador del jugador: " + marcadorJugador);
        System.out.println("Marcador del PC: " + marcadorPC);

        System.out.println("Empezemos el juego!");

        while (marcadorJugador > 0 && marcadorPC > 0) {

            System.out.println("  Tablero Jugador \t\t\t  Tablero Disparos \n");
            visualizarTablero(tableroJugador, tableroDisparosJugador);

            if (menu) {
                System.out.println("\n Tablero PC \t\t\t\t Tablero Disparos PC");
                visualizarTablero(tableroPC, tableroDisparosPC);
            }

            if (disparoJugador(tableroDisparosJugador, tableroPC)) {
                marcadorJugador--;
                if (marcadorJugador == 0) {
                    break;
                }
            } else {
                System.out.println("Agua! que pena..");
            }

            if (disparoPC(tableroDisparosPC, tableroJugador)) {
                marcadorPC--;
                if (marcadorPC == 0) {
                    break;
                }
            }
        }

        if (marcadorJugador == 0) {
            System.out.println("Enhorabuena el jugador " + nombre + " ha ganado la partida!!");
        } else {
            System.out.println("Has perdido la partida. El ganador es el PC");
        }

        try {
            new Sonido("win.wav");
            Thread.sleep(10000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo para borrar la pantalla
     */
    private static void borrarPantalla() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Método para inicializar el tablero con todo agua
     * @param tablero
     */
    public static void inicializarTablero(char[][] tablero) {

        for (int fila = 0; fila < tablero[0].length; ++fila) {

            tablero[fila][0] = (char) (65 + fila);

            for (int columna = 1; columna < tablero[0].length; ++columna) {
                if (fila != tablero[0].length - 1) {
                    tablero[fila][columna] = '~';
                } else {
                    tablero[fila][0] = ' ';

                    for (columna = 1; columna < tablero[0].length; columna++) {
                        tablero[fila][columna] = (char) (48 + columna - 1);
                    }
                }
            }
        }
    }

    /**
     * Método para visualizar el tablero y el tablero de disparos por pantalla
     * @param tablero
     * @param tableroDisparos
     */
    public static void visualizarTablero(char[][] tablero, char[][] tableroDisparos) {

        String cyan = "\u001B[36m";
        String reset = "\u001B[0m";

        for (int fila = 0; fila < tablero[0].length; fila++) {
            for (int columna = 0; columna < tablero[0].length; ++columna) {
                if (tablero[fila][columna] == '~') {
                    System.out.print(cyan + tablero[fila][columna] + " " + reset);
                } else {
                    System.out.print(tablero[fila][columna] + " ");
                }
            }

            System.out.print("\t\t\t");

            for (int columna = 0; columna < tableroDisparos[0].length; ++columna) {
                if (tableroDisparos[fila][columna] == '~') {
                    System.out.print(cyan + tableroDisparos[fila][columna] + " " + reset);
                } else {
                    System.out.print(tableroDisparos[fila][columna] + " ");
                }
            }

            System.out.println();
        }
    }

    /**
     * Método para colocar los barcos con un menu para seleccionar que barco añadir y sus coordenadas
     * @param tablero
     * @param tableroDisparos
     * @param barcos
     */
    public static void colocarBarcos2(char[][] tablero, char[][] tableroDisparos, int[] barcos) {

        int barco;
        Scanner entrada;
        int[] barcosAux = barcos.clone();


        for (int i = 0; i < barcos.length; i++) {

            int[] copy = new int[barcosAux.length - 1];

            System.out.println("Te quedan " + (barcos.length - i) + " barcos por colocar");
            for (int j = 0; j < barcosAux.length; j++) {
                System.out.println("Indice " + j + " -> " + "barco de " + barcosAux[j] + " de longitud");
            }

            System.out.println("Selecciona el barco a colocar");
            entrada = new Scanner(System.in);

            boolean colocado = false;
            int indice = entrada.nextInt();
            barco = barcosAux[indice];

            for (int j = 0, k = 0; j < barcosAux.length; j++) {
                if (j != indice) {
                    copy[k++] = barcosAux[j];
                }
            }

            barcosAux = copy;

            int coorX, coorY, orientacion;
            String coorXString;
            do {

                System.out.println("Vamos a colocar el barco de tamaño " + barco);

                do {
                    System.out.println("Introduce la coordenada X (A-J)");
                    coorXString = entrada.next();
                    coorX = Character.valueOf(coorXString.toUpperCase().charAt(0)) - 65;

                    if (coorX >= tablero[0].length - 1) {
                        System.out.println("La coordenada introducida no esta admitida. Introduce un valor entre (A - J)");
                    }
                } while (coorX >= tablero[0].length - 1);

                do {
                    System.out.println("Introduce la coordenada Y (0-9)");
                    coorY = entrada.nextInt() + 1;

                    if (coorY > tablero[0].length - 1) {
                        System.out.println("La coordenada introducida no esta admitida. Introduce un valor entre (0 - 9)");
                    }
                } while (coorY > tablero[0].length - 1);

                System.out.println("Introduce la orientación (0 - Horizontal, 1 - Vertical)");
                orientacion = entrada.nextInt();

                colocado = colocarBarco(tablero, barco, coorX, coorY, orientacion);

                visualizarTablero(tablero, tableroDisparos);
            } while (!colocado);
        }
    }

    /*public static void colocarBarcos(char[][] tablero, char[][] tableroDisparos, int[] barcos) {

        int barco;
        Scanner entrada;

        for (int i = 0; i < barcos.length; i++) {
            entrada = new Scanner(System.in);

            boolean colocado = false;
            barco = barcos[i];

            int coorX, coorY, orientacion;
            String coorXString;
            do {

                System.out.println("Vamos a colocar el barco de tamaño " + barco);
                System.out.println("Introduce la coordenada X (A-J)");

                coorXString = entrada.next();

                coorX = Character.valueOf(coorXString.toUpperCase().charAt(0)) - 65;

                System.out.println("Introduce la coordenada Y (0-9)");
                coorY = entrada.nextInt() + 1;

                System.out.println("Introduce la orientación (0 - Horizontal, 1 - Vertical)");
                orientacion = entrada.nextInt();

                colocado = colocarBarco(tablero, barco, coorX, coorY, orientacion);

                visualizarTablero(tablero, tableroDisparos);
            } while (!colocado);
        }
    }*/


    /**
     * Método para colocar el barco en el tablero comprobando posición y si hay colisión
     * @param tablero
     * @param barco
     * @param coorX
     * @param coorY
     * @param orientacion
     * @return
     */
    private static boolean colocarBarco(char[][] tablero, int barco, int coorX, int coorY, int orientacion) {
        boolean estaColocado = false;

        if (orientacion == 0) {
            if ((coorY + barco) <= tablero[coorX].length) {
                if (!colisión(tablero, barco, coorX, coorY, orientacion)) {
                    for (int i = 0; i < barco; i++) {
                        tablero[coorX][coorY + i] = 'B';
                    }
                    estaColocado = true;
                    System.out.println("El barco está colocado.");
                } else {
                    System.out.println("Ya existe un barco en estas coordenadas. Inténtelo de nuevo.");
                }
            } else {
                System.out.println("El barco se sale del mapa. Pruebe otra coordenada.");
            }


        } else if (orientacion == 1) {
            if ((coorX + barco) < tablero[coorX].length) {
                if (!colisión(tablero, barco, coorX, coorY, orientacion)) {
                    for (int i = 0; i < barco; i++) {
                        tablero[coorX + i][coorY] = 'B';
                    }
                    estaColocado = true;
                    System.out.println("El barco está colocado.");
                } else {
                    System.out.println("Ya existe un barco en estas coordenadas. Inténtelo de nuevo.");
                }
            } else {
                System.out.println("El barco se sale del mapa. Pruebe otra coordenada.");
            }

        }

        return estaColocado;
    }

    /**
     * Método para comprobar si existe colisión con otro barco al añadir un barco en el tablero
     * @param tablero
     * @param barco
     * @param coorX
     * @param coorY
     * @param orientacion
     * @return
     */
    private static boolean colisión(char[][] tablero, int barco, int coorX, int coorY, int orientacion) {
        boolean colisión = false;
        int i = 0;

        if (orientacion == 0) {
            while (i < barco && !colisión) {

                if (tablero[coorX][coorY + i] == 'B') {
                    colisión = true;
                }
                i++;
            }
        } else if (orientacion == 1) {
            while (i < barco && !colisión) {
                if (tablero[coorX + i][coorY] == 'B') {
                    colisión = true;
                }
                i++;
            }
        }

        return colisión;
    }

    /**
     * Método para que el PC coloque sus barcos
     * @param tableroPC
     * @param barcos
     */
    private static void colocarBarcosPC(char[][] tableroPC, int[] barcos) {
        int barco;

        for (int i = 0; i < barcos.length; i++) {

            boolean colocado = false;
            barco = barcos[i];

            int coorX, coorY, orientacion;
            do {

                coorX = (int) (Math.random() * 10);
                coorY = (int) (Math.random() * 10) + 1;
                orientacion = (int) (Math.random() * 2);

                colocado = colocarBarcoPC(tableroPC, barco, coorX, coorY, orientacion);

            } while (!colocado);
        }
    }

    /**
     * Método para comprobar si existe colisión al añadir un barco en el tableroPC
     * @param tableroPC
     * @param barco
     * @param coorX
     * @param coorY
     * @param orientacion
     * @return
     */
    private static boolean colocarBarcoPC(char[][] tableroPC, int barco, int coorX, int coorY, int orientacion) {

        boolean estaColocado = false;

        if (orientacion == 0) {
            if ((coorY + barco) < tableroPC[coorX].length) {
                if (!colisión(tableroPC, barco, coorX, coorY, orientacion)) {
                    for (int i = 0; i < barco; ++i) {
                        tableroPC[coorX][coorY + i] = 'B';
                    }
                    estaColocado = true;

                }
            }


        } else if (orientacion == 1) {
            if ((coorX + barco) < tableroPC[coorX].length) {
                if (!colisión(tableroPC, barco, coorX, coorY, orientacion)) {
                    for (int i = 0; i < barco; i++) {
                        tableroPC[coorX + i][coorY] = 'B';
                    }
                    estaColocado = true;

                }
            }
        }
        return estaColocado;
    }

    /**
     * Método para sumar el marcador
     * @param barcos
     * @return
     */
    public static int sumaMarcador(int[] barcos) {

        int resultado = 0;

        for (int i = 0; i < barcos.length; i++) {
            resultado += barcos[i];
        }
        return resultado;
    }

    /**
     * Método para añadir el disparo del jugador y comprobar si ha "tocado" un barco o ha hecho agua
     * @param tableroDisparosJugador
     * @param tableroPC
     * @return
     */
    public static boolean disparoJugador(char[][] tableroDisparosJugador, char[][] tableroPC) {

        boolean acierto = false;
        Scanner entrada = new Scanner(System.in);

        System.out.println("Introduce las coordenadas de disparo ");
        System.out.println("Coordenada X: ");
        int coorX = Character.valueOf(entrada.next().toUpperCase().charAt(0)) - 65;
        System.out.println("Coordenada Y: ");
        int coorY = entrada.nextInt() + 1;

        if (tableroPC[coorX][coorY] == 'B') {
            tableroDisparosJugador[coorX][coorY] = 'T';
            tableroPC[coorX][coorY] = 'T';
            acierto = true;
            try {
                new Sonido("acierto.wav");
            } catch (IOException | UnsupportedAudioFileException | InterruptedException | LineUnavailableException e) {
                e.printStackTrace();
            }
        } else {
            tableroDisparosJugador[coorX][coorY] = '*';
            try {
                new Sonido("fallo.wav");
            } catch (IOException | UnsupportedAudioFileException | InterruptedException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }

        return acierto;

    }

    /**
     * Método para añadir el disparo del PC y comprobar si ha "tocado" o ha hecho agua
     * @param tableroDisparosPC
     * @param tableroJugador
     * @return
     */
    public static boolean disparoPC(char[][] tableroDisparosPC, char[][] tableroJugador) {

        boolean acierto = false;

        int coorX = (int) (Math.random() * 10);
        int coorY = (int) (Math.random() * 10) + 1;

        if (tableroJugador[coorX][coorY] == 'B') {
            tableroDisparosPC[coorX][coorY] = 'T';
            tableroJugador[coorX][coorY] = 'T';
            acierto = true;
            System.out.println("Tocado!!!");
        } else {
            tableroDisparosPC[coorX][coorY] = '*';
            System.out.println("Por los pelos!!");
        }

        return acierto;
    }
}