package lsovet.diolan.com;

public class ArticleHeader {
	
	private String title;
	private String serverId;
	private String date;
	private String author;
	private String thumbnail;
	private String link;
	private String text;
	
	public ArticleHeader(String serverId, String title, String date, String author,
			String thumbnail, String text, String link) {
		this.serverId = serverId;
		this.title = title;
		this.date = date;
		this.author = author;
		this.thumbnail = thumbnail;
		this.text = text;
		this.link = link;
	}

	@Override
	public String toString(){
		return toJson();
	}
	
	public String toJson(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\"postId\":").append("\"").append(serverId).append("\",\n");
		sb.append("\"date\":").append("\"").append(date).append("\",\n");
		sb.append("\"author\":").append("\"").append(author).append("\",\n");
		sb.append("\"thumbnail\":").append("\"").append(thumbnail).append("\",\n");
		sb.append("\"link\":").append("\"").append(link).append("\",\n");
		sb.append("\"title\":").append("\"").append(title).append("\",\n");
		sb.append("\"text\":").append("\"").append(text).append("\"}");
		return sb.toString();
	}
}
