package lsovet.diolan.com;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.servlet.http.*;

@SuppressWarnings("serial")
public class AtticleHeadersServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		String page = req.getParameter("page");
		String url = "http://lsovet.ru/" + (page == null ? "": "page/"+page+"/");
		
		try {
		    URL myURL = new URL(url);
		    URLConnection con = myURL.openConnection();
		    con.setRequestProperty("Accept-Charset", "UTF-8");
		    con.connect();
		    con.setConnectTimeout(10*1000);
		    byte[] responce = readByParts(con.getInputStream());		    
		    String str = new String(responce, "UTF-8");
		  
		    ArticleHeaderParser parser = new ArticleHeaderParser();
		    List<ArticleHeader> a = parser.parse(str);
		  
		    resp.setContentType("text/plain");
		    resp.setCharacterEncoding("UTF-8");
		    
		    resp.getWriter().write("[");
		    
			if (a.size() > 0) {
				if (a.size() > 1) {
					for (int i = 0; i < a.size() - 1; i++) {
						resp.getWriter().write(a.get(i).toString());
						resp.getWriter().println(",");
					}
				}
				resp.getWriter().write(a.get(a.size() - 1).toString());
				resp.getWriter().write("]");
			}
		  
		    
		} 
		catch (MalformedURLException e) { 
			resp.setStatus(400);
			resp.getWriter().println(e.getMessage());
		} 
		catch (IOException e) {   
			resp.setStatus(400);
			resp.getWriter().println(e.getMessage());
		}
		
	}
	
	
	private byte[] readByParts(InputStream is) throws IOException {
		byte[] data;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		int readBlockSize = 4048;
		byte[] readBlockBuffer = new byte[readBlockSize];
		int actual = 0;
		while ( (actual != -1)) {
			actual = is.read(readBlockBuffer, 0, readBlockSize);
			if (actual > 0) {
				dos.write(readBlockBuffer, 0, actual);
			}
		}
        data = baos.toByteArray();
		return data;
    }	
	
	    
	}
	
	
