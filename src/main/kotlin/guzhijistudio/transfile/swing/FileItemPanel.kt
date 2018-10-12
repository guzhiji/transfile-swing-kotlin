package guzhijistudio.transfile.swing

import java.util.ResourceBundle
import java.io.File
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

	private val transBundle: ResourceBundle
	private val jLabelFileName: JLabel
	private val jLabelFileSize: JLabel
	private val jProgress: JProgressBar
	private lateinit var _file: File
	var file: File
		get() = _file
		set(value) {
			_file = value
			jLabelFileName.setText(value.name)
			jLabelFileSize.setText(formatSize(value.length()))
		}

	init {

		transBundle = ResourceBundle.getBundle("guzhijistudio/transfile/swing/Bundle")

		setBackground(SystemColor.window)
		setMaximumSize(Dimension(310, 72))
		setMinimumSize(Dimension(310, 72))
		setPreferredSize(Dimension(310, 72))
		setLayout(BoxLayout(this, BoxLayout.LINE_AXIS))

		val jLabelIcon = JLabel()
		jLabelIcon.setHorizontalAlignment(SwingConstants.CENTER)
		jLabelIcon.setIcon(ImageIcon(javaClass.getResource(
			"/guzhijistudio/transfile/swing/format_unknown.png")))
		add(jLabelIcon)

		val jPanelRight = JPanel()
		jPanelRight.setPreferredSize(Dimension(260, 87))
		jPanelRight.setOpaque(false)
		jPanelRight.setLayout(GridLayout(3, 1))

		jLabelFileName = JLabel()
		jLabelFileSize = JLabel()
		jProgress = JProgressBar()

		jLabelFileName.setFont(Font(transBundle.getString(
			"FileItemPanel.jLabelFileName.font"), 0, 24))
		jLabelFileName.setText(transBundle.getString(
			"FileItemPanel.jLabelFileName.text"))
		jLabelFileSize.setText(transBundle.getString(
			"FileItemPanel.jLabelFileSize.text"))

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

