package guzhijistudio.transfile.file

import guzhijistudio.transfile.utils.SocketUtils
import java.io.File
import java.net.InetSocketAddress
import java.net.Socket

class FileSender(
		private val ip: String,
		private val port: Int,
		private val filename: String,
		private val listener: FileSenderListener
) : Thread() {

	interface FileSenderListener {
	
		fun onStart(file: File)
		fun onFileSent(file: File)
		fun onError(file: File?, msg: String)
		fun onProgress(file: File, sent: Long, total: Long, speed: Long, secs: Long)
	}

	private val progressListener = object : SocketUtils.Progress {
	
		override fun onStart(file: File) {
			listener.onStart(file)
		}

		override fun onFinish(file: File) {
			listener.onFileSent(file)
		}

		override fun onProgress(file: File, progress: Long, total: Long, speed: Long, secs: Long) {
			listener.onProgress(file, progress, total, speed, secs)
		}

	}

	override fun run() {
		val buf = ByteArray(10240)
		val f = File(filename)
		if (!f.canRead()) {
			listener.onError(null, "cannot read " + f.name)
			return
		}
		try {
			val socket = Socket()
			socket.connect(InetSocketAddress(ip, port))
			socket.use { s ->
				s .getOutputStream().use {
					SocketUtils.writeString(it, "file")
					SocketUtils.writeFile(it, buf, filename, progressListener)
					SocketUtils.writeString(it, "close")
				}
			}
		} catch (e: Exception) {
			listener.onError(f, e.message ?: "")
		}
	}

}

