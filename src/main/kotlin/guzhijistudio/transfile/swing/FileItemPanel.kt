package guzhijistudio.transfile.swing

import java.awt.*
import java.awt.event.ActionEvent
import java.util.ResourceBundle
import java.io.File
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


class FileItemPanel(f: File) : JPanel() {

	private val transBundle: ResourceBundle = ResourceBundle.getBundle("guzhijistudio/transfile/swing/Bundle")
	private val jPopupMenu1: JPopupMenu
	private val jLabelIcon: JLabel
	private val jMenuItemResend: JMenuItem
	private val jMenuItemRemove: JMenuItem
	private val jLabelFileName: JLabel
	private val jLabelFileSize: JLabel
	private val transProgress: String
	private val jProgress: JProgressBar
	private lateinit var _file: File
	var file: File
		get() = _file
		set(value) {
			_file = value
			jLabelFileName.text = value.name
			jLabelFileSize.text = formatSize(value.length())
			jLabelIcon.icon = getIcon(value)
		}
	var destIp: String? = null
	var progressing = false
		set(value) {
			field = value
			if (value) done = false
		}
	var done = false
		set(value) {
			field = value
			if (value) {
				progressing = false
				jLabelFileSize.text = formatSize(file.length())
			}
		}

	companion object {
		val fileExtensions = HashMap<String, String>()
		init {
			fileExtensions[".apk"] = "apk"
			fileExtensions[".xls"] = "excel"
			fileExtensions[".xlsx"] = "excel"
			fileExtensions[".htm"] = "html"
			fileExtensions[".html"] = "html"
			fileExtensions[".mpg"] = "media"
			fileExtensions[".mpeg"] = "media"
			fileExtensions[".ogg"] = "media"
			fileExtensions[".flv"] = "media"
			fileExtensions[".mov"] = "media"
			fileExtensions[".avi"] = "media"
			fileExtensions[".mp4"] = "media"
			fileExtensions[".mp3"] = "music"
			fileExtensions[".wav"] = "music"
			fileExtensions[".mid"] = "music"
			fileExtensions[".flac"] = "music"
			fileExtensions[".pdf"] = "pdf"
			fileExtensions[".jpg"] = "picture"
			fileExtensions[".jpeg"] = "picture"
			fileExtensions[".bmp"] = "picture"
			fileExtensions[".png"] = "picture"
			fileExtensions[".gif"] = "picture"
			fileExtensions[".ppt"] = "ppt"
			fileExtensions[".pptx"] = "ppt"
			fileExtensions[".txt"] = "text"
			fileExtensions[".doc"] = "word"
			fileExtensions[".docx"] = "word"
			fileExtensions[".zip"] = "zip"
			fileExtensions[".7z"] = "zip"
			fileExtensions[".tar"] = "zip"
			fileExtensions[".gz"] = "zip"
			fileExtensions[".rar"] = "zip"
		}
	}

	init {
		transProgress = transBundle.getString("FileItemPanel.progress")
		background = SystemColor.window
		maximumSize = Dimension(310, 72)
		minimumSize = Dimension(310, 72)
		preferredSize = Dimension(310, 72)
		layout = BoxLayout(this, BoxLayout.LINE_AXIS)

		jPopupMenu1 = JPopupMenu()
		jMenuItemResend = JMenuItem()
		jMenuItemResend.text = transBundle.getString(
				"FileItemPanel.jMenuItemResend.text")
		jPopupMenu1.add(jMenuItemResend)
		jMenuItemRemove = JMenuItem()
		jMenuItemRemove.text = transBundle.getString(
				"FileItemPanel.jMenuItemRemove.text")
		jPopupMenu1.add(jMenuItemRemove)
		jPopupMenu1.isVisible = false
		jMenuItemResend.isVisible = false
		jMenuItemRemove.isVisible = false

		addMouseListener(object: MouseAdapter() {
			override fun mouseClicked(evt: MouseEvent) {
				jPopupMenu1.show(evt.component, evt.x, evt.y)
			}
			override fun mouseEntered(evt: MouseEvent) {
				background = SystemColor.control
			}
			override fun mouseExited(evt: MouseEvent) {
				background = SystemColor.window
			}
		})

		jLabelIcon = JLabel()
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

	fun setProgress(progress: Long, total: Long, speed: Long, secs: Long) {
		jProgress.maximum = 10000
		jProgress.value = (10000.0 * progress / total).toInt()
		jLabelFileSize.text = String.format(transProgress,
				formatSize(progress),
				formatSize(total),
				formatSize(speed),
				formatTime(secs))
		progressing = true
	}

	fun setError(msg: String?) {
		if (msg == null) {
			toolTipText = null
		} else {
			toolTipText = msg
			background = Color.PINK
			progressing = false
		}
	}

	fun setResendAction(actionListener: (ActionEvent) -> Unit) {
		jPopupMenu1.isVisible = true
		jMenuItemResend.isVisible = true
		jMenuItemResend.addActionListener(actionListener)
	}

	fun setRemoveAction(actionListener: (ActionEvent) -> Unit) {
		jPopupMenu1.isVisible = true
		jMenuItemRemove.isVisible = true
		jMenuItemRemove.addActionListener(actionListener)
	}

	private fun getIcon(file: File): ImageIcon {
		val name = file.name
		val pos = name.lastIndexOf('.')
		val ext = if (pos > -1) name.substring(pos).toLowerCase() else ""
		val type = fileExtensions[ext] ?: "unknown"
		return ImageIcon(javaClass.getResource(
				"/guzhijistudio/transfile/swing/format_$type.png"))
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

