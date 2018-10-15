package guzhijistudio.transfile.swing

import java.util.ResourceBundle
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.BoxLayout
import javax.swing.WindowConstants
import javax.swing.JPanel
import javax.swing.JDialog
import javax.swing.JTextField
import javax.swing.JLabel
import javax.swing.JButton


class ConfigDialog : JDialog() {

	val transBundle: ResourceBundle
	val jTextFieldDeviceName: JTextField
	val jTextFieldGroupAddr: JTextField
	val jTextFieldDir: JTextField

	init {

		transBundle = ResourceBundle.getBundle("guzhijistudio/transfile/swing/Bundle")

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
		setTitle(transBundle.getString("ConfigDialog.title"))
		setLocationByPlatform(true)
		setModal(true)
		setResizable(false)

		val jLabelDeviceName = JLabel()
		jTextFieldDeviceName = JTextField()
		jTextFieldDeviceName.setPreferredSize(Dimension(150, 27))
		jLabelDeviceName.setText(transBundle.getString(
			"ConfigDialog.jLabelDeviceName.text"))
		jLabelDeviceName.setLabelFor(jTextFieldDeviceName)

		val jLabelGroupAddr = JLabel()
		jTextFieldGroupAddr = JTextField()
		jTextFieldGroupAddr.setPreferredSize(Dimension(150, 27))
		jLabelGroupAddr.setText(transBundle.getString(
			"ConfigDialog.jLabelGroupAddr.text"))
		jLabelGroupAddr.setLabelFor(jTextFieldGroupAddr)

		val jLabelDir = JLabel()
		jTextFieldDir = JTextField()
		jTextFieldDir.setPreferredSize(Dimension(150, 27))
		jLabelDir.setText(transBundle.getString(
			"ConfigDialog.jLabelDir.text"))
		jLabelDir.setLabelFor(jTextFieldDir)

		val jPanel1 = JPanel()
		jPanel1.setLayout(GridLayout(3, 2))
		jPanel1.add(jLabelDeviceName)
		jPanel1.add(jTextFieldDeviceName)
		jPanel1.add(jLabelGroupAddr)
		jPanel1.add(jTextFieldGroupAddr)
		jPanel1.add(jLabelDir)
		jPanel1.add(jTextFieldDir)

		val jButtonSave = JButton()
		jButtonSave.setText(transBundle.getString(
			"ConfigDialog.jButtonSave.text"))
		jButtonSave.addActionListener { _ ->
			println("save")
		}
		val jButtonCancel = JButton()
		jButtonCancel.setText(transBundle.getString(
			"ConfigDialog.jButtonCancel.text"))
		jButtonCancel.addActionListener { _ ->
			dispose()
		}
		val jPanel2 = JPanel()
		jPanel2.setLayout(BoxLayout(jPanel2, BoxLayout.LINE_AXIS))
		jPanel2.add(jButtonSave)
		jPanel2.add(jButtonCancel)

		getContentPane().setLayout(
			BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS))
		getContentPane().add(jPanel1)
		getContentPane().add(jPanel2)

		pack()
	}

}

