# basicSql

#### JIRA
when you open a branch, make sure that IT IS ON A REPOSITORY WITH YOUR NAME IN IT.<br>
if you don't see your branch its because you opened it on the wrong repository.<br>

https://start.spring.io/  -> spring boot - 2.5.2

#### hello world
controller/StudentsController.java
```java
@RestController
@RequestMapping("/api/students")
public class StudentsController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> hello()
    {
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }

}
```
commit - hello world
#### swagger
```
		<dependency>
			<groupId>io.springfox</groupId>
			<artifactId>springfox-swagger-ui</artifactId>
			<version>2.6.1</version>
		</dependency><!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger2 -->
		<dependency>
			<groupId>io.springfox</groupId>
			<artifactId>springfox-swagger2</artifactId>
			<version>2.6.1</version>
		</dependency>
```



####
config/SwaggerConfig.java
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
```
http://localhost:8080/swagger-ui.html#
<br>
commit - with swagger
### START DOCKER
```
docker run -d -p 5432:5432 -v postgresdata:/var/lib/postgresql/data -e POSTGRES_PASSWORD=postgres postgres
docker ps
docker logs [containerid]
```
NOTE: IF TABLEPLUS IS NOT CONNECTING: run from in terminal 
```
~/Downloads/tableplus_work.sh  
```
and password is sample123
```
docker-compose.yml
```
version: "3"
services:
  db:
    image: postgres
    environment:
      POSTGRES_PASSWORD: postgres
    ports:
    - 5432:5432
    volumes:
      - ./postgresdata:/var/lib/postgresql/data
    privileged: true
```
REMEMBER TO KILL THE DOCKER THAT WAS RUNNING BEFORE AS IT IS USING THE SAME PORT (5432) <br>
```
docker-compose up -d
<br>
commit - with docker compose
#### Spring DATA
```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>

		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>

		<dependency>
			<groupId>org.hibernate</groupId>
			<artifactId>hibernate-validator</artifactId>
			<version>6.1.5.Final</version>
		</dependency>

		<dependency>
			<groupId>joda-time</groupId>
			<artifactId>joda-time</artifactId>
			<version>2.10.13</version>
		</dependency>
```
try to run app, will not load
<br>
application.properties:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

#JPA properties
spring.jpa.show-sql = true
spring.jpa.hibernate.ddl-auto = update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```
app will run - still no tables
<br>

util/Dates.java
```java
import org.joda.time.*;
import org.springframework.lang.Nullable;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class Dates {
    public static SimpleDateFormat shortDate = new SimpleDateFormat("YYYY-MM-dd");
    public static TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Jerusalem");

    public Dates() {
    }

    public static String dateToStr(@Nullable LocalDate date) {
        return date == null ? null : shortDate.format(date);
    }

    public static Date atUtc(LocalDateTime date) {
        return atUtc(date, TIME_ZONE);
    }

    public static Date atUtc(LocalDateTime date, TimeZone zone) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTimeZone(zone);
        calendar.set(date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth());//convert from locatDateTime to Calender time
        calendar.set(Calendar.HOUR_OF_DAY, date.getHourOfDay());
        calendar.set(Calendar.MINUTE, date.getMinuteOfHour());
        calendar.set(Calendar.SECOND, date.getSecondOfMinute());
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date atUtc(@Nullable LocalDate date) {
        return atUtc(date, TIME_ZONE);
    }

    public static Date atUtc(@Nullable LocalDate date, TimeZone zone) {
        return date == null ? null : atUtc(date.toLocalDateTime(LocalTime.MIDNIGHT), zone);
    }

    public static LocalDateTime atLocalTime(Date date) {
        return atLocalTime(date, TIME_ZONE);
    }

    public static LocalDateTime atLocalTime(Date date, TimeZone zone) {
        if (date == null) return null;
        var localDate = OffsetDateTime.ofInstant(date.toInstant(), zone.toZoneId()).toLocalDateTime();
        Calendar c = Calendar.getInstance();
        c.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        c.set(Calendar.HOUR_OF_DAY, localDate.getHour());
        c.set(Calendar.MINUTE, localDate.getMinute());
        c.set(Calendar.SECOND, localDate.getSecond());
        c.set(Calendar.MILLISECOND, 0);
        LocalDateTime res = LocalDateTime.fromCalendarFields(c);
        return res;
    }

    public static Date nowUTC() {
        return DateTime.now().withZone(DateTimeZone.UTC).toDate();
    }

    public static String getFullDateTime() {
        return DateTime.now().withZone(DateTimeZone.UTC).toDateTimeISO().toString();
    }

    public static boolean equals(@Nullable Date date1, @Nullable Date date2) {
        if (date1 != null && date2 != null) {
            return date1.getTime() == date2.getTime();
        } else {
            return Objects.equals(date1, date2);
        }
    }
}
```

model/Student.java
```java
@Entity
@Table(name="student")
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false, updatable = false)
    private Date createdAt = Dates.nowUTC();

    @NotEmpty
    @Length(max = 60)
    private String fullname;

    private Date birthDate;

    @Min(100)
    @Max(800)
    private Integer satScore;

    @Min(30)
    @Max(110)
    private Double graduationScore;

    @Length(max = 20)
    private String phone;

    @Length(max = 500)
    private String profilePicture;

}
```
explain builder plugin
<br>
commit - with spring data

#### repository and service

```
		<dependency>
			<groupId>com.fasterxml.jackson.datatype</groupId>
			<artifactId>jackson-datatype-joda</artifactId>
			<version>2.12.3</version>
		</dependency>
```

repo/StudentRepository.java
```java
public interface StudentRepository extends CrudRepository<Student,Long> {
}
```
repo/StudentService.java
```java
@Service
public class StudentService {

    @Autowired
    StudentRepository repository;

    public Iterable<Student> all() {
        return repository.findAll();
    }

    public Optional<Student> findById(Long id) {
        return repository.findById(id);
    }


    public Student save(Student student) {
        return repository.save(student);
    }

    public void delete(Student student) {
        repository.delete(student);
    }

}
```
model/StudentIn.java
```java
public class StudentIn implements Serializable {

    @Length(max = 60)
    private String fullname;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;

    @Min(100)
    @Max(800)
    private Integer satScore;

    @Min(30)
    @Max(110)
    private Double graduationScore;

    @Length(max = 20)
    private String phone;


    public Student toStudent() {
        return aStudent().createdAt(Dates.nowUTC()).birthDate(Dates.atUtc(birthDate)).fullname(fullname)
                .satScore(satScore).graduationScore(graduationScore)
                .phone(phone)
                .build();
    }

    public void updateStudent(Student student) {
        student.setBirthDate(Dates.atUtc(birthDate));
        student.setFullname(fullname);
        student.setSatScore(satScore);
        student.setGraduationScore(graduationScore);
        student.setPhone(phone);
    }

}
```

Student.java
```java
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
@JsonProperty("createdAt")
public LocalDateTime calcCreatedAt() {
        return Dates.atLocalTime(createdAt);
        }

@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
@JsonProperty("birthDate")
public LocalDateTime calcBirthDate() {
        return Dates.atLocalTime(birthDate);
        }
```

controller/StudentsController.java
```java
    @Autowired
    StudentService studentService;

@RequestMapping(value = "", method = RequestMethod.GET)
public ResponseEntity<?> getAllStudents()
        {
        return new ResponseEntity<>(studentService.all(), HttpStatus.OK);
        }

@RequestMapping(value = "/{id}", method = RequestMethod.GET)
public ResponseEntity<?> getOneStudent(@PathVariable Long id)
        {
        return new ResponseEntity<>(studentService.findById(id), HttpStatus.OK);
        }

@RequestMapping(value = "", method = RequestMethod.POST)
public ResponseEntity<?> insertStudent(@RequestBody StudentIn studentIn)
        {
        Student student = studentIn.toStudent();
        student = studentService.save(student);
        return new ResponseEntity<>(student, HttpStatus.OK);
        }

@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody StudentIn student)
        {
        Optional<Student> dbStudent = studentService.findById(id);
        if (dbStudent.isEmpty()) throw new RuntimeException("Student with id: " + id + " not found");
        student.updateStudent(dbStudent.get());
        Student updatedStudent = studentService.save(dbStudent.get());
        return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
        }

@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
public ResponseEntity<?> deleteStudent(@PathVariable Long id)
        {
        Optional<Student> dbStudent = studentService.findById(id);
        if (dbStudent.isEmpty()) throw new RuntimeException("Student with id: " + id + " not found");
        studentService.delete(dbStudent.get());
        return new ResponseEntity<>("DELETED", HttpStatus.OK);
        }
```
commit - with students CRUD + REST

###FPS - Filter Pagination Sort

####simple filter
StudentRepository.java
```java
    List<Student> findAllBySatScoreGreaterThan(Integer satScore);
```


StudentService.java
```java
    public List<Student> getStudentWithSatHigherThan(Integer sat) {
        return repository.findAllBySatScoreGreaterThan(sat);
        }
```


StudentController.java
```java
    @RequestMapping(value = "/highSat", method = RequestMethod.GET)
public ResponseEntity<?> getHighSatStudents(@RequestParam Integer sat)
        {
        return new ResponseEntity<>(studentService.getStudentWithSatHigherThan(sat), HttpStatus.OK);
        }
```

####FPS
apply fps.patch
<br>
model/StudentOut:
```java
@Entity
@SqlResultSetMapping(name = "StudentOut")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentOut {

    @Id
    private Long id;

    private Date createdat;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdat")
    public LocalDateTime calcCreatedAt() {
        return Dates.atLocalTime(createdat);
    }

    private String fullname;
    private Date birthdate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("birthdate")
    public LocalDateTime calcBirthDate() {
        return Dates.atLocalTime(birthdate);
    }

    private Integer satscore;
    private Double graduationscore;

    private String phone;
    private String profilepicture;

    public Integer getSatScore() {
        return satscore;
    }

    public Double getGraduationScore() {
        return graduationscore;
    }

    public Date getCreatedat() {
        return createdat;
    }

    public String getFullname() {
        return fullname;
    }

    public Date getBirthdate() {
        return birthdate;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public String getProfilePicture() {
        return profilepicture;
    }
}

```

model/StudentSortField.java
```java
public enum StudentSortField {
    id("id") ,
    createdAt ("created_at"),
    fullName ("fullname"),
    birthDate ("birth_date"),
    satScore ("sat_score"),
    graduationScore ("graduation_score"),
    phone ("phone"),
    profilepicture ("profile_picture");

    public final String fieldName;
    private StudentSortField(String fieldName) {
        this.fieldName = fieldName;
    }
}
```
StudentsController.java
```java
    @Autowired
    EntityManager em;

@Autowired
    ObjectMapper om;

@RequestMapping(value = "", method = RequestMethod.GET)
public ResponseEntity<PaginationAndList> search(@RequestParam(required = false) String fullName,
@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromBirthDate,
@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toBirthDate,
@RequestParam(required = false) Integer fromSatScore,
@RequestParam(required = false) Integer toSatScore,
@RequestParam(defaultValue = "1") Integer page,
@RequestParam(defaultValue = "50") @Min(1) Integer count,
@RequestParam(defaultValue = "id") StudentSortField sort, @RequestParam(defaultValue = "asc") SortDirection sortDirection) throws JsonProcessingException {

        var res =aFPS().select(List.of(
        aFPSField().field("id").alias("id").build(),
        aFPSField().field("created_at").alias("createdat").build(),
        aFPSField().field("fullname").alias("fullname").build(),
        aFPSField().field("birth_date").alias("birthdate").build(),
        aFPSField().field("sat_score").alias("satscore").build(),
        aFPSField().field("graduation_score").alias("graduationscore").build(),
        aFPSField().field("phone").alias("phone").build(),
        aFPSField().field("profile_picture").alias("profilepicture").build()
        ))
        .from(List.of(" student s"))
        .conditions(List.of(
        aFPSCondition().condition("( lower(fullname) like :fullName )").parameterName("fullName").value(likeLowerOrNull(fullName)).build(),
        aFPSCondition().condition("( s.birth_Date >= :fromBirthDate )").parameterName("fromBirthDate").value(atUtc(fromBirthDate)).build(),
        aFPSCondition().condition("( s.birth_Date <= :toBirthDate )").parameterName("toBirthDate").value(atUtc(toBirthDate)).build(),
        aFPSCondition().condition("( sat_score >= :fromSatScore )").parameterName("fromSatScore").value(fromSatScore).build(),
        aFPSCondition().condition("( sat_score <= :toSatScore )").parameterName("toSatScore").value(toSatScore).build()
        )).sortField(sort.fieldName).sortDirection(sortDirection).page(page).count(count)
        .itemClass(StudentOut.class)
        .build().exec(em, om);
        return ResponseEntity.ok(res);
        }

```
commit - with FPS
### OneToMany grades
apply one_to_many_grades.patch
<br>
Student.java
```java
    @OneToMany(mappedBy = "student", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
private Collection<StudentGrade> studentGrades = new ArrayList<>();
```
StudentOut.java
```java
    private Double avgscore;
```
StudentsController.java
```java
    public ResponseEntity<PaginationAndList> search(@RequestParam(required = false) String fullName,
@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromBirthDate,
@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toBirthDate,
@RequestParam(required = false) Integer fromSatScore,
@RequestParam(required = false) Integer toSatScore,
@RequestParam(required = false) Integer fromAvgScore,
@RequestParam(defaultValue = "1") Integer page,
@RequestParam(defaultValue = "50") @Min(1) Integer count,
@RequestParam(defaultValue = "id") StudentSortField sort, @RequestParam(defaultValue = "asc") SortDirection sortDirection) throws JsonProcessingException {

        var res =aFPS().select(List.of(
        aFPSField().field("s.id").alias("id").build(),
        aFPSField().field("s.created_at").alias("createdat").build(),
        aFPSField().field("s.fullname").alias("fullname").build(),
        aFPSField().field("s.birth_date").alias("birthdate").build(),
        aFPSField().field("s.sat_score").alias("satscore").build(),
        aFPSField().field("s.graduation_score").alias("graduationscore").build(),
        aFPSField().field("s.phone").alias("phone").build(),
        aFPSField().field("s.profile_picture").alias("profilepicture").build(),
        aFPSField().field("(select avg(sg.course_score) from  student_grade sg where sg.student_id = s.id ) ").alias("avgscore").build()
        ))
        .from(List.of(" student s"))
        .conditions(List.of(
        aFPSCondition().condition("( lower(fullname) like :fullName )").parameterName("fullName").value(likeLowerOrNull(fullName)).build(),
        aFPSCondition().condition("( s.birth_Date >= :fromBirthDate )").parameterName("fromBirthDate").value(atUtc(fromBirthDate)).build(),
        aFPSCondition().condition("( s.birth_Date <= :toBirthDate )").parameterName("toBirthDate").value(atUtc(toBirthDate)).build(),
        aFPSCondition().condition("( sat_score >= :fromSatScore )").parameterName("fromSatScore").value(fromSatScore).build(),
        aFPSCondition().condition("( sat_score <= :toSatScore )").parameterName("toSatScore").value(toSatScore).build(),
        aFPSCondition().condition("( (select avg(sg.course_score) from  student_grade sg where sg.student_id = s.id ) >= :fromAvgScore )").parameterName("fromAvgScore").value(fromAvgScore).build()
        )).sortField(sort.fieldName).sortDirection(sortDirection).page(page).count(count)
        .itemClass(StudentOut.class)
        .build().exec(em, om);
        return ResponseEntity.ok(res);
        }

```
StudentSortField.java
```java
    id("s.id") ,
        createdAt ("s.created_at"),
        fullName ("s.fullname"),
        birthDate ("s.birth_date"),
        satScore ("s.at_score"),
        graduationScore ("s.graduation_score"),
        phone ("s.phone"),
        profilepicture ("s.profile_picture"),
        avgScore (" (select avg(sg.course_score) from  student_grade sg where sg.student_id = s.id ) ");
```
commit - with one to many

### Files & Presigned link
login to aws:<br>
aws account: 995553441267

```
		<dependency>
			<groupId>com.amazonaws</groupId>
			<artifactId>aws-java-sdk-s3</artifactId>
			<version>1.11.908</version>
		</dependency>
		
		<dependency>
			<groupId>commons-io</groupId>
			<artifactId>commons-io</artifactId>
			<version>2.11.0</version>
		</dependency>
```

application.properies
```
amazon.aws.accesskey=AKIA6PS436XZZSSPURH2
amazon.aws.secretkey=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
bucket.url=files.handson.academy
```

apply patch awsFileService
<br>

model/StudentOut.java
```java
    public static StudentOut of(Student student, AWSService awsService) {
        StudentOut res = new StudentOut();
        res.id = student.getId();
        res.createdat = student.getCreatedAt();
        res.fullname = student.getFullname();
        res.birthdate = student.getBirthDate();
        res.satscore = student.getSatScore();
        res.graduationscore = student.getGraduationScore();
        res.phone = student.getPhone();
        res.profilepicture = awsService.generateLink(student.getProfilePicture());
        res.avgscore = null;
        return res;
        }
```
controller/StudentsController.java
```java
    @RequestMapping(value = "/{id}/image", method = RequestMethod.PUT)
public ResponseEntity<?> uploadStudentImage(@PathVariable Long id,  @RequestParam("image") MultipartFile image)
        {
        Optional<Student> dbStudent = studentService.findById(id);
        if (dbStudent.isEmpty()) throw new RuntimeException("Student with id: " + id + " not found");
        String bucketPath = "apps/niv/student-" +  id + ".png" ;
        awsService.putInBucket(image, bucketPath);
        dbStudent.get().setProfilePicture(bucketPath);
        Student updatedStudent = studentService.save(dbStudent.get());
        return new ResponseEntity<>(StudentOut.of(updatedStudent, awsService) , HttpStatus.OK);
        }
```
make user of student.of in get and put
<br>
commit - AWS S3 & presigned link

### Async -> SMS integration

```
		<dependency>
			<groupId>com.squareup.okhttp3</groupId>
			<artifactId>okhttp</artifactId>
			<version>4.8.1</version>
		</dependency>
		
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-collections4</artifactId>
			<version>4.4</version>
		</dependency>
```

application.properties
```
sms4free.key=J2IX1eEa9
sms4free.user=0525236451
sms4free.password=66534228
```

util/SmsService.java
```java
import okhttp3.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


// copied from documentation https://www.sms4free.co.il/outcome-sms-api.html
@Service
public class SmsService {
    protected final Log logger = LogFactory.getLog(getClass());
    OkHttpClient client = new OkHttpClient.Builder().build();

    @Value("${sms4free.key}")
    private String ACCOUNT_KEY;
    @Value("${sms4free.user}")
    private String ACCOUNT_USER;
    @Value("${sms4free.password}")
    private String ACCOUNT_PASS;



    //Overloaded constructor to send a message to both a phone number and an email address.
    public boolean send(String text, String phoneNumber) {

        if (phoneNumber == null) return false;
        MediaType mediaType = MediaType.parse("application/text");
        String url = "https://www.sms4free.co.il/ApiSMS/SendSMS";

        String key = ACCOUNT_KEY;
        String user = ACCOUNT_USER;
        String pass = ACCOUNT_PASS;
        try {

            RequestBody formBody = new FormBody.Builder()
                    .add("key", key)
                    .add("user", user)
                    .add("pass", pass)
                    .add("sender", "HANDSON")
                    .add("recipient", phoneNumber)
                    .add("msg", text)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .addHeader("content-type", "application/json")
                    .addHeader("Accept-Language", "en-US,en;q=0.5")
                    .build();

            String data = client.newCall(request).execute().body().string();
            //print result
            boolean success = Integer.parseInt(data) > 0;

            return success;
        }catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
```
controller/studentsController.java
```java
    @Autowired
    SmsService smsService;

@RequestMapping(value = "/sms/all", method = RequestMethod.POST)
public ResponseEntity<?> smsAll(@RequestParam String text)
        {
        new Thread(()-> {
        IteratorUtils.toList(studentService.all().iterator())
        .parallelStream()
        .map(student -> student.getPhone())
        .filter(phone -> !isEmpty(phone))
        .forEach(phone -> smsService.send(text, phone));
        }).start();
        return new ResponseEntity<>("SENDING", HttpStatus.OK);
        }
```
check: https://www.sms4free.co.il/SMSLog.html

commit - Async -> SMS integration
### Microservices
copy to basicSpring-ms <br>
use notepad to edit pom.xml add -ms to name and artifactId
<br>
model/MessageAndPhones.java
```java
public class MessageAndPhones {
    String message;
    List<String> phones;

    public String getMessage() {
        return message;
    }

    public List<String> getPhones() {
        return phones;
    }

    public MessageAndPhones(String message, List<String> phones) {
        this.message = message;
        this.phones = phones;
    }
}
```
controller/SmsController.java
```java
@RestController
@RequestMapping("/api")
public class SmsController {

    @Autowired
    SmsService smsService;

    @RequestMapping(value = "/sms", method = RequestMethod.POST)
    public ResponseEntity<?> smsAll(@RequestBody MessageAndPhones messageAndPhones)
    {
        new Thread(()-> {
            messageAndPhones.getPhones()
                    .parallelStream()
                    .forEach(phone -> smsService.send(messageAndPhones.getMessage(), phone));

        }).start();
        return new ResponseEntity<>("SENDING", HttpStatus.OK);
    }
}
```
#### call the ms
config/RestTemplateConfig.java
```java
@Component
public class RestTemplateConfig {

    @Bean
    @RequestScope
    public RestTemplate getRestTemplate(HttpServletRequest inReq) {
        final String authHeader =
                inReq.getHeader(HttpHeaders.AUTHORIZATION);
        final RestTemplate restTemplate = new RestTemplate();
        if (authHeader != null && !authHeader.isEmpty()) {
            restTemplate.getInterceptors().add(
                    (outReq, bytes, clientHttpReqExec) -> {
                        outReq.getHeaders().set(
                                HttpHeaders.AUTHORIZATION, authHeader
                        );
                        return clientHttpReqExec.execute(outReq, bytes);
                    });
        }
        return restTemplate;
    }
}
```
model/MessageAndPhones.java
```java
public class MessageAndPhones {
    String message;
    List<String> phones;

    public String getMessage() {
        return message;
    }

    public List<String> getPhones() {
        return phones;
    }

    public MessageAndPhones(String message, List<String> phones) {
        this.message = message;
        this.phones = phones;
    }
}
```
application.properties
```
sms.ms.url=http://localhost:8081/
```
util/SmsService.java
```java
    @Value("${sms.ms.url}")
    String SMS_MS_URL;
    protected final Log logger = LogFactory.getLog(getClass());
    OkHttpClient client = new OkHttpClient.Builder().build();

    @Autowired
    RestTemplate  rTemplate;

    public String sendSms(MessageAndPhones messageAndPhones) {
        return rTemplate.postForObject(SMS_MS_URL + "/api/sms/", messageAndPhones, String.class);
    }
```
controller/StudentsController.java
```java
    @RequestMapping(value = "/sms/all", method = RequestMethod.POST)
    public ResponseEntity<?> smsAll(@RequestParam String text)
    {
        List<String> phones =
            IteratorUtils.toList(studentService.all().iterator())
                    .parallelStream()
                    .map(student -> student.getPhone())
                    .filter(phone -> !isEmpty(phone))
                    .collect(Collectors.toList());
        return new ResponseEntity<>(smsService.send(new MessageAndPhones(text, phones)), HttpStatus.OK);
    }
```
StudentsControllerTest.java
```java
        verify(smsService, atLeastOnce()).send(any());
```
### JWT
```
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt</artifactId>
			<version>0.9.1</version>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>

```
apply jwt patch
<br>
https://bcrypt-generator.com/
<br>

config/SwaggerConfig.java
```java
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String DEFAULT_INCLUDE_PATTERN = "/.*";

    @Bean
    public Docket swaggerSpringfoxDocket() {
        Contact contact = new Contact(
                "handson",
                "https://hansdon-academy.com",
                "admin@handson-academy.com");

        List<VendorExtension> vext = new ArrayList<>();

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .apiInfo(ApiInfo.DEFAULT)
                .forCodeGeneration(true)
                .genericModelSubstitutes(ResponseEntity.class)
                .ignoredParameterTypes(Pageable.class)
                .ignoredParameterTypes(java.sql.Date.class)
                .directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
                .directModelSubstitute(java.time.LocalDateTime.class, Date.class)
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.newArrayList(apiKey()))
                .useDefaultResponseMessages(false);

        docket = docket.select()
                .paths(regex(DEFAULT_INCLUDE_PATTERN))
                .build();
        return docket;
    }


    private ApiKey apiKey() {
        return new ApiKey("Authorization", AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(regex(DEFAULT_INCLUDE_PATTERN))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
                new SecurityReference("Authorization", authorizationScopes));
    }
}
```
commit - with JWT

```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
```
apply patch -> exception_handling.patch
<br>

in studentsController change RuntimeException to HandsonException<br>
commit - with actuator & global exception <br>
### Test & Dockerize
```java

		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-lang3</artifactId>
			<version>3.4</version>
		</dependency>

		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>4.12</version>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<version>1.4.200</version>
			<scope>test</scope>
		</dependency>
```

application.properties
```
spring.datasource.url=jdbc:postgresql://postgres:5432/postgres
```
sudo vi /etc/hosts (add 127.0.0.1 postgres, 127.0.0.1 appserver
<br>
apply path test_docker.patch
<br>
docker build . -t basic-spring
<br>
docker-compose -f docker-compose-local.yml up --force-recreate
<br>
docker login
<br>
nivitzhaky
<br>
Jul201789#
<br>
docker tag basic-spring  nivitzhaky/basic-spring:niv-001
<br>
docker push nivitzhaky/basic-spring:niv-001
<br>
in docker-comopse-local.yml change image nivitzhaky/basic-spring:niv-001
<br>
test coverage
<br>
commit - with tests & dockrize

### POSTMAN & NEWMAN
apply patch postman_newman.patch
<br>
import into postman
<br>
docker-compose -f docker-compose-ci.yml up -d --force-recreate --build
<br>
docker-compose -f docker-compose-ci.yml run wait -c server:8080 -t 120
<br>
docker exec  niv-basicspring_newman_1 newman run STUDENTS_TEST.postman_collection.json --reporters cli,junit,htmlextra --reporter-junit-export "newman/report.xml" --reporter-htmlextra-export "newman/report.html" 
<br>
check test/newman/report.html

commit - with postman newman

### Jenkins
pull request
http://ec2-3-125-50-55.eu-central-1.compute.amazonaws.com:8080/
<br>
user:student
<br>
password:student

### EC2
<br>
docker login
<br>
<b>user:</b>hoacademy
<br>
<b>password:</b>Hands-On!

```
docker tag basic-spring  hoacademy/basic-spring:niv-001
docker push hoacademy/basic-spring:niv-001
```

login to aws: https://995553441267.signin.aws.amazon.com/console
<br>
change pem permissions
```
chmod 400 ~/Downloads/ec2.pem

sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo curl -L https://github.com/docker/compose/releases/download/1.22.0/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

```
echo "
version: \"3\"
services:
  appserver:
    container_name: server
    hostname: localhost
    image: nivitzhaky/basic-spring:niv-001
    ports:
      - "8080:8080"
  postgres:
    image: postgres
    environment:
      POSTGRES_PASSWORD: postgres
    ports:
    - 5432:5432
    volumes:
      - ./postgresdata:/var/lib/postgresql/data
    privileged: true
" >>  docker-compose-aws.yml
```
sudo  /usr/local/bin/docker-compose -f docker-compose-aws.yml up -d
<br>
connect with:
<br>
http://[your machine]:8080/swagger-ui.html
<br>
<b>don't forget to terminate the machine</b>
### openshift

https://console-openshift-console.apps.cluster.oshift.xyz/
<br>
kubeadmin
<br>
xn4NF-MwGgM-CVEYe-RMneJ
<br>
