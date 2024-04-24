import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Scanner;
import java.text.DecimalFormat;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Crear una instancia de HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // Construir la solicitud GET a la API de tasas de cambio
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://v6.exchangerate-api.com/v6/2efe8e90827ea90a3f2b73e2/latest/USD"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        // Enviar la solicitud y procesar la respuesta
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Obtener el cuerpo de la respuesta
            String responseBody = response.body();

            // Utilizar Gson para convertir el JSON en un objeto Java
            JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);

            // Acceder al objeto conversion_rates que contiene los códigos de moneda y sus valores de conversión
            JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");

            boolean salir = false;
            while (!salir) {
                System.out.println("Menú de Conversión:");
                System.out.println("1. Convertir USD a otra moneda");
                System.out.println("2. Convertir otra moneda a USD");
                System.out.println("3. Salir");
                System.out.print("Ingrese el número de la opción deseada: ");

                // Validar la entrada del usuario
                int opcion = obtenerEnteroValidado(scanner);

                switch (opcion) {
                    case 1:
                        convertirUSDaOtraMoneda(conversionRates);
                        break;
                    case 2:
                        convertirOtraMonedaAUSD(conversionRates);
                        break;
                    case 3:
                        salir = true;
                        System.out.println("¡Hasta luego!");
                        break;
                    default:
                        System.out.println("Opción no válida. Por favor, ingrese un número válido.");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al enviar la solicitud: " + e.getMessage());
        }
    }

    public static void convertirUSDaOtraMoneda(JsonObject conversionRates) {
        // Lógica para convertir USD a otra moneda
        Scanner scanner = new Scanner(System.in);

        // Validar el monto ingresado
        double amountUSD = obtenerDoubleValidado(scanner, "Ingrese el monto en USD: ");

        System.out.print("Ingrese el código de la moneda de destino (por ejemplo, EUR): ");
        String targetCurrency = scanner.next();

        if (conversionRates.has(targetCurrency)) {
            double conversionRate = conversionRates.get(targetCurrency).getAsDouble();
            double convertedAmount = amountUSD * conversionRate;

            // Formatear el valor convertido
            DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
            String formattedAmount = df.format(convertedAmount);

            System.out.println(amountUSD + " USD equivale a " + formattedAmount + " " + targetCurrency);
        } else {
            System.out.println("La moneda de destino ingresada no está disponible.");
        }
    }

    public static void convertirOtraMonedaAUSD(JsonObject conversionRates) {
        // Lógica para convertir otra moneda a USD
        Scanner scanner = new Scanner(System.in);

        // Validar el monto ingresado
        double amount = obtenerDoubleValidado(scanner, "Ingrese el monto en la moneda de origen: ");

        System.out.print("Ingrese el código de la moneda de origen (por ejemplo, EUR): ");
        String sourceCurrency = scanner.next();

        if (conversionRates.has(sourceCurrency)) {
            double conversionRate = conversionRates.get(sourceCurrency).getAsDouble();
            double convertedAmount = amount / conversionRate;
            System.out.println(amount + " " + sourceCurrency + " equivale a " + convertedAmount + " USD");
        } else {
            System.out.println("La moneda de origen ingresada no está disponible.");
        }
    }

    // Método para obtener un entero validado
    public static int obtenerEnteroValidado(Scanner scanner) {
        while (true) {
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            } else {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.next(); // Limpiar el buffer del scanner
            }
        }
    }

    // Método para obtener un double validado
    public static double obtenerDoubleValidado(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            if (scanner.hasNextDouble()) {
                double numero = scanner.nextDouble();
                if (numero > 0) {
                    return numero;
                } else {
                    System.out.println("El número debe ser mayor que cero.");
                }
            } else {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.next(); // Limpiar el buffer del scanner
            }
        }
    }
}
