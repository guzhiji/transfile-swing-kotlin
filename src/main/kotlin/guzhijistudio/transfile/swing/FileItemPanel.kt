package guzhijistudio.transfile.swing

import java.util.ResourceBundle
import java.io.File
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.SystemColor
import java.awt.Font
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.SwingConstants
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JPanel
import javax.swing.JLabel
import javax.swing.JProgressBar


class FileItemPanel(f: File) : JPanel() {

	private val transBundle: ResourceBundle = ResourceBundle.getBundle("guzhijistudio/transfile/swing/Bundle")
	private val jLabelFileName: JLabel
	private val jLabelFileSize: JLabel
	private val jProgress: JProgressBar
	private lateinit var _file: File
	var file: File
		get() = _file
		set(value) {
			_file = value
			jLabelFileName.text = value.name
			jLabelFileSize.text = formatSize(value.length())
		}

	init {

		background = SystemColor.window
		maximumSize = Dimension(310, 72)
		minimumSize = Dimension(310, 72)
		preferredSize = Dimension(310, 72)
		layout = BoxLayout(this, BoxLayout.LINE_AXIS)
		addMouseListener(object: MouseAdapter() {
			override fun mouseClicked(evt: MouseEvent) {}
			override fun mouseEntered(evt: MouseEvent) {
				background = SystemColor.control
			}
			override fun mouseExited(evt: MouseEvent) {
				background = SystemColor.window
			}
		})

		val jLabelIcon = JLabel()
		jLabelIcon.horizontalAlignment = SwingConstants.CENTER
		jLabelIcon.icon = ImageIcon(javaClass.getResource(
				"/guzhijistudio/transfile/swing/format_unknown.png"))
		add(jLabelIcon)

		val jPanelRight = JPanel()
		jPanelRight.preferredSize = Dimension(260, 87)
		jPanelRight.isOpaque = false
		jPanelRight.layout = GridLayout(3, 1)

		jLabelFileName = JLabel()
		jLabelFileSize = JLabel()
		jProgress = JProgressBar()

		jLabelFileName.font = Font(transBundle.getString(
				"FileItemPanel.jLabelFileName.font"), 0, 24)
		jLabelFileName.text = transBundle.getString(
				"FileItemPanel.jLabelFileName.text")
		jLabelFileSize.text = transBundle.getString(
				"FileItemPanel.jLabelFileSize.text")

		jPanelRight.add(jLabelFileName)
		jPanelRight.add(jLabelFileSize)
		jPanelRight.add(jProgress)

		add(jPanelRight)

		file = f
	}

	private fun formatTime(secs: Long): String {
		var t = secs.toFloat()
		if (t < 0)
			return transBundle.getString(
				"FileItemPanel.unit.unknown")
		if (t <= 60)
			return t.toInt().toString() + transBundle
				.getString("FileItemPanel.unit.sec")
		t /= 60f
		if (t <= 60)
			return ((t * 100).toInt() / 100.0).toString() + transBundle
				.getString("FileItemPanel.unit.min")
		t /= 60f
		return ((t * 100).toInt() / 100.0).toString() + transBundle
			.getString("FileItemPanel.unit.hr")
	}

	private fun formatSize(size: Long): String {
		var s = size.toFloat()
		if (s < 1024)
			return "$s bytes"
		s /= 1024f
		if (s < 1024)
			return "${(s * 100).toInt() / 100.0} Kb"
		s /= 1024f
		if (s < 1024)
			return "${(s * 100).toInt() / 100.0} Mb"
		s /= 1024f
		return "${(s * 100).toInt() / 100.0} Gb"
	}

}

