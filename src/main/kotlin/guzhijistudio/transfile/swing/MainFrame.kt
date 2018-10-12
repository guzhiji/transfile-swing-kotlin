package guzhijistudio.transfile.swing

import java.io.File
import java.util.ResourceBundle
import java.awt.Toolkit
import java.awt.EventQueue
import java.awt.SystemColor
import java.awt.Dimension
import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.WindowConstants
import javax.swing.JFrame
import javax.swing.JTabbedPane
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JFileChooser
import javax.swing.ImageIcon


class MainFrame : JFrame() {

	private val transBundle: ResourceBundle
	private val jPanelSendingFiles: JPanel
	private val jPanelFilesReceived: JPanel

	init {

		transBundle = ResourceBundle.getBundle("guzhijistudio/transfile/swing/Bundle")

		setIconImage(Toolkit.getDefaultToolkit().getImage(
			javaClass.getResource(
				"/guzhijistudio/transfile/swing/icon.png")))
		setTitle(transBundle.getString("MainFrame.title"))
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
		setLocationByPlatform(true)
		setPreferredSize(Dimension(350, 500))
		setResizable(false)
		setJMenuBar(createMenu())

		val jPanelSend = JPanel()
		val jPanelReceive = JPanel()

		val tabs = JTabbedPane()
		tabs.addTab(transBundle.getString(
			"MainFrame.jPanelSend.TabConstraints.tabTitle"),
			jPanelSend)
		tabs.addTab(transBundle.getString(
			"MainFrame.jPanelReceive.TabConstraints.tabTitle"),
			jPanelReceive)
		getContentPane().add(tabs, BorderLayout.CENTER)

		jPanelSend.setLayout(BoxLayout(jPanelSend, BoxLayout.PAGE_AXIS))
		jPanelReceive.setLayout(BoxLayout(jPanelReceive, BoxLayout.PAGE_AXIS))

		val jScrollPane1 = JScrollPane()
		var jScrollPane2 = JScrollPane()
		jScrollPane1.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
		jScrollPane2.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
		jPanelSend.add(createSendToolBar())
		jPanelSend.add(jScrollPane1)
		jPanelReceive.add(jScrollPane2)

		jPanelSendingFiles = JPanel()
		jPanelFilesReceived = JPanel()
		jPanelSendingFiles.setBackground(SystemColor.window)
		jPanelSendingFiles.setLayout(BoxLayout(jPanelSendingFiles, BoxLayout.PAGE_AXIS))
		jPanelFilesReceived.setBackground(SystemColor.window)
		jPanelFilesReceived.setLayout(BoxLayout(jPanelFilesReceived, BoxLayout.PAGE_AXIS))
		jScrollPane1.setViewportView(jPanelSendingFiles)
		jScrollPane2.setViewportView(jPanelFilesReceived)

		pack()
	}

	private fun createSendToolBar(): JPanel {
		val jButtonAddFile = JButton()
		var jButtonSendAll = JButton()

		jButtonAddFile.setIcon(ImageIcon(javaClass.getResource(
			"/guzhijistudio/transfile/swing/addfile.png")))
		jButtonAddFile.setText(transBundle.getString(
			"MainFrame.jButtonAddFile.text"))
		jButtonAddFile.addActionListener { _ ->
			showFileChooser()
		}

		jButtonSendAll.setIcon(ImageIcon(javaClass.getResource(
			"/guzhijistudio/transfile/swing/sendall.png")))
		jButtonSendAll.setText(transBundle.getString(
			"MainFrame.jButtonSendAll.text"))
		jButtonSendAll.addActionListener { _ ->
			showDeviceChooser()
		}

		val toolbar = JPanel()
		toolbar.setLayout(BoxLayout(toolbar, BoxLayout.LINE_AXIS))
		toolbar.add(jButtonAddFile)
		toolbar.add(jButtonSendAll)
		return toolbar
	}

	private fun createMenu(): JMenuBar {
		val menu = JMenuBar()
		val menuSend = JMenu()
		var menuConfig = JMenu()
		menuSend.setText(transBundle.getString(
			"MainFrame.jMenuSend.text"))
		menuConfig.setText(transBundle.getString(
			"MainFrame.jMenuConfig.text"))

		val jMenuItemAddFile = JMenuItem()
		val jMenuItemSendAll = JMenuItem()
		jMenuItemAddFile.setIcon(ImageIcon(javaClass.getResource(
			"/guzhijistudio/transfile/swing/addfile.png")))
		jMenuItemAddFile.setText(transBundle.getString(
			"MainFrame.jMenuItemAddFile.text"))
		jMenuItemAddFile.addActionListener { _ ->
			showFileChooser()
		}
		jMenuItemSendAll.setIcon(ImageIcon(javaClass.getResource(
			"/guzhijistudio/transfile/swing/sendall.png")))
		jMenuItemSendAll.setText(transBundle.getString(
			"MainFrame.jMenuItemSendAll.text"))
		jMenuItemSendAll.addActionListener { _ ->
			showDeviceChooser()
		}
		menuSend.add(jMenuItemAddFile)
		menuSend.add(jMenuItemSendAll)

		val jMenuItemConfig = JMenuItem()
		jMenuItemConfig.setIcon(ImageIcon(javaClass.getResource(
			"/guzhijistudio/transfile/swing/config.png")))
		jMenuItemConfig.setText(transBundle.getString(
			"MainFrame.jMenuItemConfig.text"))
		jMenuItemConfig.addActionListener { _ ->
			ConfigDialog().isVisible = true
		}
		menuConfig.add(jMenuItemConfig)

		menu.add(menuSend)
		menu.add(menuConfig)
		return menu
	}

	private fun showFileChooser() {
		val chooser = JFileChooser()
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
		chooser.setMultiSelectionEnabled(true)
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			for (file in chooser.selectedFiles) {
				if (findFileItemPanel(jPanelSendingFiles, file) == null) {
					println(file.name)
					val panel = FileItemPanel(file)
					jPanelSendingFiles.add(panel)
					jPanelSendingFiles.repaint()
				}
			}
		}
	}

	private fun showDeviceChooser() {
		val dialog = DeviceListDialog()
		dialog.isVisible = true
	}

	private fun findFileItemPanel(list: JPanel, file: File): FileItemPanel? {
		for (comp in list.components) {
			if (comp is FileItemPanel) {
				val fileItem = comp as FileItemPanel
				if (fileItem.file.equals(file))
					return fileItem
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


