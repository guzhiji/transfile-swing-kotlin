package guzhijistudio.transfile.swing

import guzhijistudio.transfile.file.FileReceiver
import guzhijistudio.transfile.file.FileReceiver.FileReceiverListener
import guzhijistudio.transfile.file.FileSender
import guzhijistudio.transfile.file.FileSender.FileSenderListener
import guzhijistudio.transfile.identity.Broadcaster
import guzhijistudio.transfile.utils.Config
import guzhijistudio.transfile.utils.Constants
import java.io.File
import java.util.ResourceBundle
import java.awt.Toolkit
import java.awt.EventQueue
import java.awt.SystemColor
import java.awt.Dimension
import java.awt.BorderLayout
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import javax.swing.*
import kotlin.system.exitProcess

class MainFrame : JFrame() {

	private val transBundle: ResourceBundle = ResourceBundle.getBundle("guzhijistudio/transfile/swing/Bundle")
	private val fileSenders = Executors.newFixedThreadPool(2)
	private val jPanelSendingFiles: JPanel
	private val jPanelFilesReceived: JPanel

	private var broadcaster: Broadcaster? = null
	private var fileReceiver: FileReceiver? = null

	init {

		/*
		for (info in UIManager.getInstalledLookAndFeels())
			if ("Nimbus" == info.name) {
				UIManager.setLookAndFeel(info.className)
				break
			}
		*/

		iconImage = Toolkit.getDefaultToolkit().getImage(
				javaClass.getResource(
						"/guzhijistudio/transfile/swing/icon.png"))
		title = transBundle.getString("MainFrame.title")
		defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
		isLocationByPlatform = true
		preferredSize = Dimension(350, 500)
		isResizable = false
		jMenuBar = createMenu()

		val jPanelSend = JPanel()
		val jPanelReceive = JPanel()

		val tabs = JTabbedPane()
		tabs.addTab(transBundle.getString(
			"MainFrame.jPanelSend.TabConstraints.tabTitle"),
			jPanelSend)
		tabs.addTab(transBundle.getString(
			"MainFrame.jPanelReceive.TabConstraints.tabTitle"),
			jPanelReceive)
		contentPane.add(tabs, BorderLayout.CENTER)

		jPanelSend.layout = BoxLayout(jPanelSend, BoxLayout.PAGE_AXIS)
		jPanelReceive.layout = BoxLayout(jPanelReceive, BoxLayout.PAGE_AXIS)

		val jScrollPane1 = JScrollPane()
		val jScrollPane2 = JScrollPane()
		jScrollPane1.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
		jScrollPane2.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
		jPanelSend.add(createSendToolBar())
		jPanelSend.add(jScrollPane1)
		jPanelReceive.add(jScrollPane2)

		jPanelSendingFiles = JPanel()
		jPanelFilesReceived = JPanel()
		jPanelSendingFiles.background = SystemColor.window
		jPanelSendingFiles.layout = BoxLayout(jPanelSendingFiles, BoxLayout.PAGE_AXIS)
		jPanelFilesReceived.background = SystemColor.window
		jPanelFilesReceived.layout = BoxLayout(jPanelFilesReceived, BoxLayout.PAGE_AXIS)
		jScrollPane1.setViewportView(jPanelSendingFiles)
		jScrollPane2.setViewportView(jPanelFilesReceived)

		pack()
	}

	private val frListener = object : FileReceiverListener {
		override fun onFileReceived(file: File) {
			findFileItemPanel(jPanelFilesReceived, file)?.done = true
		}

		override fun onFile(file: File) {
			val fileItem = findFileItemPanel(jPanelFilesReceived, file)
			if (fileItem == null) {
				val panel = FileItemPanel(file)
				panel.setRemoveAction {
					if (panel.progressing) {
						JOptionPane.showMessageDialog(this@MainFrame, transBundle.getString(
								"MainFrame.Message.FileReceiving"))
					} else {
						jPanelFilesReceived.remove(panel)
						jPanelFilesReceived.revalidate()
						jPanelFilesReceived.repaint()
					}
				}
				jPanelFilesReceived.add(panel)
				jPanelFilesReceived.revalidate()
			}
		}

		override fun onMsg(msg: String) {
		}

		override fun onError(msg: String) {
		}

		override fun onProgress(file: File, received: Long, total: Long, speed: Long, secs: Long) {
			findFileItemPanel(jPanelFilesReceived, file)?.setProgress(
					received, total, speed, secs)
		}
	}

	private val fsListener = object : FileSenderListener {
		override fun onStart(file: File) {
			findFileItemPanel(jPanelSendingFiles, file)?.setError(null)
		}

		override fun onFileSent(file: File) {
			findFileItemPanel(jPanelSendingFiles, file)?.done = true
		}

		override fun onError(file: File?, msg: String) {
			if (file != null)
				findFileItemPanel(jPanelSendingFiles, file)?.setError(msg)
		}

		override fun onProgress(file: File, sent: Long, total: Long, speed: Long, secs: Long) {
			findFileItemPanel(jPanelSendingFiles, file)?.setProgress(
					sent, total, speed, secs)
		}
	}

	init {
		if (Config.LOADED) {
			startBroadcast()
			startFileReceiver()
		} else {
			showConfigDialog(true)
		}
	}

	private fun startBroadcast() {
		val groupAddr = InetSocketAddress(
				Config.GROUP_ADDR,
				Constants.IDENTITY_SERVER_PORT)
		broadcaster = Broadcaster(Config.DEVICE_NAME, groupAddr)
		broadcaster!!.start()
	}

	private fun startFileReceiver() {
		val dir = File(Config.DIR)
		fileReceiver = FileReceiver(Constants.FILE_SERVER_PORT, dir, frListener)
		fileReceiver!!.start()
	}

	private fun showConfigDialog(quitOnCancel: Boolean) {
		val dialog = ConfigDialog()
		dialog.isVisible = true
		if (dialog.isSaved) {
			fileReceiver?.shutdown()
			broadcaster?.shutdown()
			try {
				fileReceiver?.join()
				startFileReceiver()
				broadcaster?.join()
				startBroadcast()
			} catch (ex: InterruptedException) {
				JOptionPane.showMessageDialog(this, ex.message)
				exitProcess(1)
			}
		} else if (quitOnCancel) {
			exitProcess(0)
		}
	}

	private fun createSendToolBar(): JPanel {
		val jButtonAddFile = JButton()
		val jButtonSendAll = JButton()

		jButtonAddFile.icon = ImageIcon(javaClass.getResource(
				"/guzhijistudio/transfile/swing/addfile.png"))
		jButtonAddFile.text = transBundle.getString(
				"MainFrame.jButtonAddFile.text")
		jButtonAddFile.addActionListener {
			showFileChooser()
		}

		jButtonSendAll.icon = ImageIcon(javaClass.getResource(
				"/guzhijistudio/transfile/swing/sendall.png"))
		jButtonSendAll.text = transBundle.getString(
				"MainFrame.jButtonSendAll.text")
		jButtonSendAll.addActionListener {
			showDeviceChooser()
		}

		val toolbar = JPanel()
		toolbar.layout = BoxLayout(toolbar, BoxLayout.LINE_AXIS)
		toolbar.add(jButtonAddFile)
		toolbar.add(jButtonSendAll)
		return toolbar
	}

	private fun createMenu(): JMenuBar {
		val menu = JMenuBar()
		val menuSend = JMenu()
		val menuConfig = JMenu()
		menuSend.text = transBundle.getString(
				"MainFrame.jMenuSend.text")
		menuConfig.text = transBundle.getString(
				"MainFrame.jMenuConfig.text")

		val jMenuItemAddFile = JMenuItem()
		val jMenuItemSendAll = JMenuItem()
		jMenuItemAddFile.icon = ImageIcon(javaClass.getResource(
				"/guzhijistudio/transfile/swing/addfile.png"))
		jMenuItemAddFile.text = transBundle.getString(
				"MainFrame.jMenuItemAddFile.text")
		jMenuItemAddFile.addActionListener {
			showFileChooser()
		}
		jMenuItemSendAll.icon = ImageIcon(javaClass.getResource(
				"/guzhijistudio/transfile/swing/sendall.png"))
		jMenuItemSendAll.text = transBundle.getString(
				"MainFrame.jMenuItemSendAll.text")
		jMenuItemSendAll.addActionListener {
			showDeviceChooser()
		}
		menuSend.add(jMenuItemAddFile)
		menuSend.add(jMenuItemSendAll)

		val jMenuItemConfig = JMenuItem()
		jMenuItemConfig.icon = ImageIcon(javaClass.getResource(
				"/guzhijistudio/transfile/swing/config.png"))
		jMenuItemConfig.text = transBundle.getString(
				"MainFrame.jMenuItemConfig.text")
		jMenuItemConfig.addActionListener {
			ConfigDialog().isVisible = true
		}
		menuConfig.add(jMenuItemConfig)

		menu.add(menuSend)
		menu.add(menuConfig)
		return menu
	}

	private fun showFileChooser() {
		val chooser = JFileChooser()
		chooser.fileSelectionMode = JFileChooser.FILES_ONLY
		chooser.isMultiSelectionEnabled = true
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			for (file in chooser.selectedFiles) {
				if (findFileItemPanel(jPanelSendingFiles, file) == null) {
					println(file.name)
					val panel = FileItemPanel(file)
					panel.setResendAction {
						if (panel.progressing) {
							JOptionPane.showMessageDialog(this@MainFrame, transBundle.getString(
									"MainFrame.Message.FileSending"))
						} else {
							var ip = panel.destIp
							if (ip == null) {
								val dialog = DeviceListDialog()
								dialog.isVisible = true
								if (dialog.isIpSelected) {
									ip = dialog.selectedIp
									panel.destIp = ip
								}
							}
							if (ip != null) {
								fileSenders.submit(FileSender(ip, Constants.FILE_SERVER_PORT,
										panel.file.absolutePath, fsListener))
							}
						}
					}
					panel.setRemoveAction {
						if (panel.progressing) {
							JOptionPane.showMessageDialog(this@MainFrame, transBundle.getString(
									"MainFrame.Message.FileSending"))
						} else {
							jPanelSendingFiles.remove(panel)
							jPanelSendingFiles.revalidate()
							jPanelSendingFiles.repaint()
						}
					}
					jPanelSendingFiles.add(panel)
				} else {
					JOptionPane.showMessageDialog(this, String.format(
							transBundle.getString("MainFrame.Message.FileInList"),
							file.name))
				}
			}
			// revalidate()
			jPanelSendingFiles.revalidate()
		}
	}

	private fun showDeviceChooser() {
		val dialog = DeviceListDialog()
		dialog.isVisible = true
		if (dialog.isIpSelected) {
			for (comp in jPanelSendingFiles.components) {
				if (comp is FileItemPanel) {
					if (!comp.progressing && !comp.done) {
						comp.destIp = dialog.selectedIp
						fileSenders.submit(FileSender(
								dialog.selectedIp!!, Constants.FILE_SERVER_PORT,
								comp.file.absolutePath, fsListener))
					}
				}
			}
		}
	}

	private fun findFileItemPanel(list: JPanel, file: File): FileItemPanel? {
		for (comp in list.components) {
			if (comp is FileItemPanel) {
				if (comp.file == file)
					return comp
			}
		}
		return null
	}

	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			EventQueue.invokeLater {
				MainFrame().isVisible = true
			}
		}
	}
}

