package ubicomplab.org.predictivecalendar;

import java.io.Serializable;

class CalendarEvents implements Serializable {
	private static final long serialVersionUID = -7060210544600464481L;
	private String name;
	private long startTime;
	private long endTime;
	private String location;
	private int id;
	private boolean status;
	private long differenceBtwEvents;
	private String result;
	
	private ProblemType problem;
	
	public CalendarEvents() {

	}

	public CalendarEvents(String name, long startTime, long endTime,
			String location, int eventId) {
		setId(eventId);
		setName(name);
		setStartTime(startTime);
		setendTime(endTime);
		setLocation(location);

	}

	// getter
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public String getLocation() {
		return location;
	}

	public boolean getStatus() {
		return status;
	}

	public long getDifferenceBtwEvents() {
		return differenceBtwEvents;
	}

	public String getResult()
	{
		return result;
	}
	
	public ProblemType getProblemType()
	{
		return problem;
	}
	
	// setter

	public void setId(int eventId) {
		this.id = eventId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setendTime(long endTime) {
		this.endTime = endTime;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void setDifferenceBtwEvents(long DifferenceBtwEvents) {
		this.differenceBtwEvents = DifferenceBtwEvents;
	}
	
	public void setResult(String result)
	{
		this.result = result;
	}
	
	public void setProblemType(ProblemType prob)
	{
		this.problem = prob;
	}

}