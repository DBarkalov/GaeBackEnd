package lsovet.diolan.com;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;

@SuppressWarnings("serial")
public class AtticleHeadersServlet extends HttpServlet {
	private static final String ATTICLE_HEADERS = "AtticleHeaders";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		final String page = req.getParameter("page");
		final String url = "http://lsovet.ru/"	+ (page == null ? "" : "page/" + page + "/");
		Key articleKey = KeyFactory.createKey(ATTICLE_HEADERS, url);

		String content = getContentFromCache(articleKey);
		if (content != null) {
			setOkResponce(resp, content);
		} else {
			byte[] responceData = null;
			try {
				responceData = getContentFromNet(url);
			} catch (IOException e) {
				setErrorResponce(resp, 400, "fetch url error");
			}
			if (responceData != null) {
				ArticleHeaderParser parser = new ArticleHeaderParser();
				final List<ArticleHeader> articles = parser.parse(new String(responceData, "UTF-8"));
				if (articles.size() > 0) {
					content = createContent(articles);
					setOkResponce(resp, content);
					saveContentToCache(articleKey, content);
				} else {
					setErrorResponce(resp, 400, "parse data error");
				}
			}
		}

	}


	private void setOkResponce(HttpServletResponse resp, String content)
			throws IOException {
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(content);
	}

	private void setErrorResponce(HttpServletResponse resp, int status,
			String content) throws IOException {
		resp.setStatus(status);
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(content);
	}

	private String createContent(List<ArticleHeader> articles) {
		if (articles.size() == 0) {
			throw new IllegalArgumentException("size must > 0");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (articles.size() > 1) {
			for (int i = 0; i < articles.size() - 1; i++) {
				sb.append(articles.get(i).toString());
				sb.append(",");
			}
		}
		sb.append(articles.get(articles.size() - 1).toString());
		sb.append("]");
		return sb.toString();
	}

	private void saveContentToCache(Key key, String content) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity(key);
		entity.setProperty("content", new Text(content));
		entity.setProperty("time", new Date().getTime());
		datastore.put(entity);
	}
	
	private String getContentFromCache(final Key key) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity headerEntity = null;
		try {
			headerEntity = datastore.get(key);
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}
		// check entity ttl
		if (headerEntity.hasProperty("time")) {
			long time = (Long) headerEntity.getProperty("time");
			long liveSec = (new Date().getTime() - time) * 1000;
			if (liveSec < 60) { // ttl = 1 min
				((Text) headerEntity.getProperty("content")).getValue();
			}
		}
		return null;
	}

	private byte[] getContentFromNet(final String url) throws IOException {
		URL myURL = new URL(url);
		URLConnection con = myURL.openConnection();
		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.connect();
		con.setConnectTimeout(8 * 1000);
		return readByParts(con.getInputStream());
	}

	private byte[] readByParts(InputStream is) throws IOException {
		byte[] data;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		int readBlockSize = 4048;
		byte[] readBlockBuffer = new byte[readBlockSize];
		int actual = 0;
		while ((actual != -1)) {
			actual = is.read(readBlockBuffer, 0, readBlockSize);
			if (actual > 0) {
				dos.write(readBlockBuffer, 0, actual);
			}
		}
		data = baos.toByteArray();
		return data;
	}

}
