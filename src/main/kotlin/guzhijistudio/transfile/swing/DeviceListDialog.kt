package guzhijistudio.transfile.swing

import java.util.ResourceBundle
import javax.swing.WindowConstants
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JDialog
import javax.swing.JScrollPane
import javax.swing.JButton
import javax.swing.JList
import javax.swing.ListSelectionModel
import javax.swing.AbstractListModel


class DeviceListDialog : JDialog() {

	private val transBundle: ResourceBundle
	private val jListDevices: JList<String>
	private val deviceListModel: DeviceListModel
	var selectedIp: String? = null
	val isIpSelected: Boolean
		get() = selectedIp != null

	init {

		transBundle = ResourceBundle.getBundle("guzhijistudio/transfile/swing/Bundle")

		setTitle(transBundle.getString("DeviceListDialog.title"))
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
		setLocationByPlatform(true)
		setModal(true)
		setResizable(false)

		getContentPane().setLayout(BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS))

		jListDevices = JList()
		jListDevices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
		deviceListModel = DeviceListModel()
		jListDevices.setModel(deviceListModel)

		val jScrollPane1 = JScrollPane()
		jScrollPane1.setViewportView(jListDevices)

		getContentPane().add(jScrollPane1)
		getContentPane().add(createControlPanel())

		pack()
	}

	private fun createControlPanel(): JPanel {
		val p = JPanel()
		p.setLayout(BoxLayout(p, BoxLayout.LINE_AXIS))
		val jButtonOk = JButton()
		val jButtonCancel = JButton()
		jButtonOk.setText(transBundle.getString(
			"DeviceListDialog.jButtonOk.text"))
		jButtonOk.addActionListener { _ ->
			println("ok")
		}
		jButtonCancel.setText(transBundle.getString(
			"DeviceListDialog.jButtonCancel.text"))
		jButtonCancel.addActionListener { _ ->
			dispose()
		}
		p.add(jButtonOk)
		p.add(jButtonCancel)
		return p
	}

	private data class DeviceItem(
			var ip: String,
			var name: String
	)

	private class DeviceListModel() : AbstractListModel<String>() {
		private val devices = ArrayList<DeviceItem>()

		override fun getSize(): Int {
			return devices.size
		}

		override fun getElementAt(index: Int): String {
			val device = devices[index]
			return "${device.name} - ${device.ip}"
		}

		fun add(device: DeviceItem) {
			devices.add(device)
			val index = devices.size - 1
			fireIntervalAdded(this, index, index)
		}

		fun remove(ip: String) {
			var s: Int? = null
			var i: Int = -1
			for (device in devices) {
				i++
				if (device.ip == ip) {
					s = i
					break
				}
			}
			if (s != null) {
				devices.removeAt(s)
				fireIntervalRemoved(this, s, s)
			}
		}
	}

}
