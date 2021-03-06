package guzhijistudio.transfile.swing

import guzhijistudio.transfile.identity.UdpServer
import guzhijistudio.transfile.utils.Config
import guzhijistudio.transfile.utils.Constants
import java.net.InetAddress
import java.net.InetSocketAddress
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

	private val transBundle: ResourceBundle = ResourceBundle.getBundle("guzhijistudio/transfile/swing/Bundle")
	private val jListDevices: JList<String>
	private val deviceListModel: DeviceListModel
	var selectedIp: String? = null
	val isIpSelected: Boolean
		get() = selectedIp != null

	init {
		title = transBundle.getString("DeviceListDialog.title")
		defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
		isLocationByPlatform = true
		isModal = true
		isResizable = false

		contentPane.layout = BoxLayout(contentPane, BoxLayout.PAGE_AXIS)

		jListDevices = JList()
		jListDevices.selectionMode = ListSelectionModel.SINGLE_SELECTION
		deviceListModel = DeviceListModel()
		jListDevices.model = deviceListModel

		val jScrollPane1 = JScrollPane()
		jScrollPane1.setViewportView(jListDevices)

		contentPane.add(jScrollPane1)
		contentPane.add(createControlPanel())

		pack()
	}

	private val usListener = object : UdpServer.UdpServerListener {
		override fun onEnter(ip: String, name: String) {
			println("enter:$ip")
			deviceListModel.add(DeviceItem(ip, name))
		}
		override fun onQuit(ip: String) {
			println("quit:$ip")
			deviceListModel.remove(ip)
		}
	}
	private val server: UdpServer

	init {
		val addr = InetSocketAddress("0.0.0.0", Constants.IDENTITY_SERVER_PORT)
		val group = InetAddress.getByName(Config.GROUP_ADDR)
		server = UdpServer(addr, group, usListener)
		server.start()
	}

	override fun dispose() {
		server.shutdown()
		super.dispose()
	}

	private fun createControlPanel(): JPanel {
		val p = JPanel()
		p.layout = BoxLayout(p, BoxLayout.LINE_AXIS)
		val jButtonOk = JButton()
		val jButtonCancel = JButton()
		jButtonOk.text = transBundle.getString(
				"DeviceListDialog.jButtonOk.text")
		jButtonOk.addActionListener {
			val index = jListDevices.selectedIndex
			if (index > -1) {
				selectedIp = deviceListModel.get(index).ip
				dispose()
			}
		}
		jButtonCancel.text = transBundle.getString(
				"DeviceListDialog.jButtonCancel.text")
		jButtonCancel.addActionListener {
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

	private class DeviceListModel : AbstractListModel<String>() {
		private val devices = ArrayList<DeviceItem>()

		override fun getSize(): Int {
			return devices.size
		}

		override fun getElementAt(index: Int): String {
			val device = devices[index]
			return "${device.name} - ${device.ip}"
		}

		fun get(index: Int): DeviceItem {
			return devices[index]
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

