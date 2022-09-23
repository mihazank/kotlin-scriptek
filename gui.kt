import javax.swing.*
import javax.swing.text.*
import java.awt.event.*

class Gui : JFrame() {
    private var usernameField: JTextField
    private var passwordField: JPasswordField
    private var loginButton: JButton

    fun open(name: String, login: (String, String) -> Unit) {
        loginButton.addActionListener {
            login(usernameField.text, String(passwordField.password))
        }

        setSize(295, 240)
        title = name
        isResizable = false
        isVisible = true
    }

    init {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        JFrame.setDefaultLookAndFeelDecorated(true)

        contentPane.layout = null

        JLabel("username").apply {
            setBounds(20, 20, 100, 20)
            contentPane.add(this)
        }
        usernameField = JTextField().apply {
            setBounds(20, 40, 240, 20)
            contentPane.add(this)
        }

        JLabel("password").apply {
            setBounds(20, 70, 100, 20)
            contentPane.add(this)
        }
        passwordField = JPasswordField().apply {
            setBounds(20, 90, 240, 20)
            contentPane.add(this)
        }

        loginButton = JButton("login").apply {
            setBounds(20, 130, 240, 20)
            contentPane.add(this)
        }
    }

    fun close() {
        dispatchEvent(WindowEvent(this, WindowEvent.WINDOW_CLOSING))
    }
}