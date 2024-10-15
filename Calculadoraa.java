import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculadoraa extends JFrame implements ActionListener {
    private JTextField display;
    private String operador = "";
    private double numero1 = 0;
    private boolean nuevaOperacion = true;

    private String funcionUsuario = "sin"; // Función por defecto

    private String[] botones = {
            "7", "8", "9", "+", "cos", "^", "C",
            "4", "5", "6", "-", "sen", "(", "±",
            "1", "2", "3", "*", "tan", ")", "∫",
            "=", "0", ".", "/", "x", "e", "dx"
    };

    public Calculadoraa() {
        setTitle("Calculadora Científica");
        setSize(350, 450);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("SansSerif", Font.PLAIN, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBackground(new Color(245, 245, 245));
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(display, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(4, 7, 5, 5)); // 4 filas y 7 columnas
        panelBotones.setBackground(new Color(255, 255, 255));

        for (String text : botones) {
            JButton button = new JButton(text);
            button.setFont(new Font("SansSerif", Font.PLAIN, 18));
            button.setFocusPainted(false);
            button.setBackground(new Color(220, 220, 220));
            button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            button.addActionListener(this);
            panelBotones.add(button);
        }

        add(panelBotones, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        if (comando.equals("C")) {
            display.setText("");
            operador = "";
            numero1 = 0;
            nuevaOperacion = true;
        } else if (comando.equals("±")) {
            if (!display.getText().isEmpty()) {
                try {
                    double valor = Double.parseDouble(display.getText());
                    valor *= -1;
                    display.setText(String.valueOf(valor));
                } catch (NumberFormatException ex) {
                    display.setText("Error");
                }
            }
        } else if (comando.equals("=")) {
            calcular();
            nuevaOperacion = true;
        } else if (comando.equals("+") || comando.equals("-") || comando.equals("*") || comando.equals("/")) {
            if (!display.getText().isEmpty()) {
                try {
                    numero1 = Double.parseDouble(display.getText());
                    operador = comando;
                    nuevaOperacion = true;
                } catch (NumberFormatException ex) {
                    display.setText("Error");
                }
            }
        } else if (comando.equals("^")) {
            if (!display.getText().isEmpty()) {
                try {
                    numero1 = Double.parseDouble(display.getText());
                    operador = "^";
                    nuevaOperacion = true;
                } catch (NumberFormatException ex) {
                    display.setText("Error");
                }
            }
        } else if (comando.equals("cos") || comando.equals("sen") || comando.equals("tan")) {
            aplicarFuncionTrigonometrica(comando);
        } else if (comando.equals("∫")) {
            calcularIntegral();
        } else {
            if (nuevaOperacion) {
                display.setText("");
                nuevaOperacion = false;
            }
            if (comando.equals(".") && display.getText().contains(".")) {
                return;
            }
            display.setText(display.getText() + comando);
        }
    }

    private void calcular() {
        if (!display.getText().isEmpty() && !operador.isEmpty()) {
            try {
                double numero2 = Double.parseDouble(display.getText());
                double resultado = 0;

                switch (operador) {
                    case "+":
                        resultado = numero1 + numero2;
                        break;
                    case "-":
                        resultado = numero1 - numero2;
                        break;
                    case "*":
                        resultado = numero1 * numero2;
                        break;
                    case "/":
                        if (numero2 != 0) {
                            resultado = numero1 / numero2;
                        } else {
                            display.setText("Error: División por cero");
                            return;
                        }
                        break;
                    case "^":
                        resultado = Math.pow(numero1, numero2);
                        break;
                }
                display.setText(String.valueOf(resultado));
                operador = "";
            } catch (NumberFormatException ex) {
                display.setText("Error");
            }
        }
    }

    private void aplicarFuncionTrigonometrica(String funcion) {
        if (!display.getText().isEmpty()) {
            try {
                double valor = Double.parseDouble(display.getText());
                double resultado = 0;

                switch (funcion) {
                    case "cos":
                        resultado = Math.cos(Math.toRadians(valor)); 
                        break;
                    case "sen":
                        resultado = Math.sin(Math.toRadians(valor));
                        break;
                    case "tan":
                        resultado = Math.tan(Math.toRadians(valor));
                        break;
                }

                display.setText(String.valueOf(resultado));
            } catch (NumberFormatException ex) {
                display.setText("Error");
            }
        }
    }

    private void calcularIntegral() {
        try {
            funcionUsuario = JOptionPane.showInputDialog(this, "Ingresa la función (Ejemplo: 2*x^2):");
            double a = Double.parseDouble(JOptionPane.showInputDialog(this, "Ingresa el límite inferior:"));
            double b = Double.parseDouble(JOptionPane.showInputDialog(this, "Ingresa el límite superior:"));
            int n = 1000;

            // Calcular la integral usando el método trapezoidal
            double resultado = trapezoidalRule(a, b, n);
            display.setText(String.format("∫ ≈ %.6f", resultado));

        } catch (NumberFormatException ex) {
            display.setText("Syntax_Error");
        }
    }

    // Método que evalúa una función polinómica simple
    private double f(double x) {
        return evaluarFuncion(funcionUsuario, x);
    }

    private double evaluarFuncion(String funcion, double x) {
        funcion = funcion.replace("x", String.valueOf(x)); // Reemplaza x con su valor
        if (funcion.contains("^")) {
            String[] partes = funcion.split("\\^");
            double base = Double.parseDouble(partes[0]);
            double exponente = Double.parseDouble(partes[1]);
            return Math.pow(base, exponente);
        }
        return Double.parseDouble(funcion);
    }

    private double trapezoidalRule(double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0.5 * (f(a) + f(b));

        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            sum += f(x);
        }

        return sum * h;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calculadoraa calculadora = new Calculadoraa();
            calculadora.setVisible(true);
        });
    }
}
