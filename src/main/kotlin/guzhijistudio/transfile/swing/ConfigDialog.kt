package guzhijistudio.transfile.swing

import guzhijistudio.transfile.utils.Config
import java.util.ResourceBundle
import java.awt.Dimension
import java.awt.GridLayout
import java.io.File
import java.net.InetAddress
import java.net.UnknownHostException
import javax.swing.*
import kotlin.system.exitProcess


class ConfigDialog : JDialog() {

	private val transBundle: ResourceBundle = ResourceBundle.getBundle("guzhijistudio/transfile/swing/Bundle")
	private val jTextFieldDeviceName: JTextField
	private val jTextFieldGroupAddr: JTextField
	private val jTextFieldDir: JTextField

	var isSaved = false
		private set(value) {
			field = value
		}

	init {
		defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
		title = transBundle.getString("ConfigDialog.title")
		isLocationByPlatform = true
		isModal = true
		isResizable = false

		val jLabelDeviceName = JLabel()
		jTextFieldDeviceName = JTextField()
		jTextFieldDeviceName.preferredSize = Dimension(150, 27)
		jLabelDeviceName.text = transBundle.getString(
				"ConfigDialog.jLabelDeviceName.text")
		jLabelDeviceName.labelFor = jTextFieldDeviceName

		val jLabelGroupAddr = JLabel()
		jTextFieldGroupAddr = JTextField()
		jTextFieldGroupAddr.preferredSize = Dimension(150, 27)
		jLabelGroupAddr.text = transBundle.getString(
				"ConfigDialog.jLabelGroupAddr.text")
		jLabelGroupAddr.labelFor = jTextFieldGroupAddr

		val jLabelDir = JLabel()
		jTextFieldDir = JTextField()
		jTextFieldDir.preferredSize = Dimension(150, 27)
		jLabelDir.text = transBundle.getString(
				"ConfigDialog.jLabelDir.text")
		jLabelDir.labelFor = jTextFieldDir

		val jPanel1 = JPanel()
		jPanel1.layout = GridLayout(3, 2)
		jPanel1.add(jLabelDeviceName)
		jPanel1.add(jTextFieldDeviceName)
		jPanel1.add(jLabelGroupAddr)
		jPanel1.add(jTextFieldGroupAddr)
		jPanel1.add(jLabelDir)
		jPanel1.add(jTextFieldDir)

		val jButtonSave = JButton()
		jButtonSave.text = transBundle.getString(
				"ConfigDialog.jButtonSave.text")
		jButtonSave.addActionListener {
			jButtonSaveActionPerformed()
		}
		val jButtonCancel = JButton()
		jButtonCancel.text = transBundle.getString(
				"ConfigDialog.jButtonCancel.text")
		jButtonCancel.addActionListener {
			dispose()
		}
		val jPanel2 = JPanel()
		jPanel2.layout = BoxLayout(jPanel2, BoxLayout.LINE_AXIS)
		jPanel2.add(jButtonSave)
		jPanel2.add(jButtonCancel)

		contentPane.layout = BoxLayout(contentPane, BoxLayout.PAGE_AXIS)
		contentPane.add(jPanel1)
		contentPane.add(jPanel2)

		pack()

		if (Config.LOADED) {
			jTextFieldDeviceName.text = Config.DEVICE_NAME
			jTextFieldGroupAddr.text = Config.GROUP_ADDR
			jTextFieldDir.text = Config.DIR
		} else {
			jTextFieldDeviceName.text = ""
			jTextFieldGroupAddr.text = "224.0.0.255"
			jTextFieldDir.text = ""
		}
	}

	private fun validateUserInput(): Boolean {
		val deviceName = jTextFieldDeviceName.text.trim()
		if (deviceName.isEmpty()) {
			JOptionPane.showMessageDialog(this, transBundle.getString(
					"ConfigDialog.Message.EmptyDeviceName"))
			return false
		}

		val sGroupAddr = jTextFieldGroupAddr.text.trim()
		if (sGroupAddr.isEmpty()) {
			JOptionPane.showMessageDialog(this, transBundle.getString(
					"ConfigDialog.Message.EmptyGroupAddr"))
			return false
		}
		try {
			val groupAddr = InetAddress.getByName(sGroupAddr)
			if (groupAddr == null) {
				JOptionPane.showMessageDialog(this, transBundle.getString(
						"ConfigDialog.Message.InvalidGroupAddr"))
				return false
			} else if (!groupAddr.isMulticastAddress) {
				JOptionPane.showMessageDialog(this, transBundle.getString(
						"ConfigDialog.Message.NotMulticastAddr"))
				return false
			}
		} catch (_: UnknownHostException) {
			JOptionPane.showMessageDialog(this, transBundle.getString(
					"ConfigDialog.Message.InvalidGroupAddr"))
			return false
		}

		val sDir = jTextFieldDir.text.trim()
		if (sDir.isEmpty()) {
			JOptionPane.showMessageDialog(this, transBundle.getString(
					"ConfigDialog.Message.EmptyDir"))
			return false
		}
		val dir = File(sDir)
		if (!dir.exists()) {
			JOptionPane.showMessageDialog(this, transBundle.getString(
					"ConfigDialog.Message.DirNotExist"))
			return false
		} else if (!dir.isDirectory) {
			JOptionPane.showMessageDialog(this, transBundle.getString(
					"ConfigDialog.Message.PathNotDir"))
			return false
		} else if (!dir.canWrite()) {
			JOptionPane.showMessageDialog(this, transBundle.getString(
					"ConfigDialog.Message.DirNotWritable"))
			return false
		}
		return true
	}

	private fun jButtonSaveActionPerformed() {
		if (validateUserInput()) {
			Config.DEVICE_NAME = jTextFieldDeviceName.text.trim()
			Config.GROUP_ADDR = jTextFieldGroupAddr.text.trim()
			Config.DIR = jTextFieldDir.text.trim()
			if (Config.save()) {
				isSaved = true
				dispose()
			} else {
				JOptionPane.showMessageDialog(this, transBundle.getString(
						"ConfigDialog.Message.ConfigSaveFailure"))
				exitProcess(1)
			}
		}
	}

}

