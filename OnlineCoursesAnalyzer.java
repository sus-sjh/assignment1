import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This is just a demo for you, please run it on JDK17. This is just a demo, and you can extend and
 * implement functions based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {
  List<Course> courses = new ArrayList<>();

  public OnlineCoursesAnalyzer(String datasetPath) {
    BufferedReader br = null;
    String line;
    try {
      br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
      br.readLine();
      while ((line = br.readLine()) != null) {
        String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
        Course course =
            new Course(
                info[0],
                info[1],
                new Date(info[2]),
                info[3],
                info[4],
                info[5],
                Integer.parseInt(info[6]),
                Integer.parseInt(info[7]),
                Integer.parseInt(info[8]),
                Integer.parseInt(info[9]),
                Integer.parseInt(info[10]),
                Double.parseDouble(info[11]),
                Double.parseDouble(info[12]),
                Double.parseDouble(info[13]),
                Double.parseDouble(info[14]),
                Double.parseDouble(info[15]),
                Double.parseDouble(info[16]),
                Double.parseDouble(info[17]),
                Double.parseDouble(info[18]),
                Double.parseDouble(info[19]),
                Double.parseDouble(info[20]),
                Double.parseDouble(info[21]),
                Double.parseDouble(info[22]));
        courses.add(course);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public Map<String, Integer> getPtcpCountByInst() {

    Map<String, Integer> map = new TreeMap<>();
    for (Course course : courses) {
      String institution = course.institution;
      int count = course.participants;
      if (map.containsKey(institution)) {
        map.put(institution, map.get(institution) + count);
      } else {
        map.put(institution, count);
      }
    }
    return map;
  }

  public Map<String, Integer> getPtcpCountByInstAndSubject() {

    Map<String, Integer> map = new TreeMap<>();
    for (Course course : courses) {
      String institution = course.institution;
      String subject = course.subject;
      int count = course.participants;
      String key = institution + "-" + subject;
      if (map.containsKey(key)) {
        map.put(key, map.get(key) + count);
      } else {
        map.put(key, count);
      }
    }
    List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
    Collections.sort(
        list,
        new Comparator<Map.Entry<String, Integer>>() {
          @Override
          public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            if (o1.getValue().equals(o2.getValue())) {
              return o1.getKey().compareTo(o2.getKey());
            } else {
              return o2.getValue() - o1.getValue();
            }
          }
        });
    Map<String, Integer> sortedMap = new LinkedHashMap<>();
    for (Map.Entry<String, Integer> entry : list) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }
    return sortedMap;
  }

  public Map<String, List<List<String>>> getCourseListOfInstructor() {
    Map<String, List<List<String>>> map = new TreeMap<>();
    for (Course course : courses) {
      String[] instructor = course.instructors.split(", ");
      String title = course.title;
      if (instructor.length == 1) {
        if (map.containsKey(instructor[0])) {
          boolean flag = false;
          for (String list : map.get(instructor[0]).get(0)) {
            if (list.equals(title)) {
              flag = true;
              break;
            }
          }
          if (!flag) {
            map.get(instructor[0]).get(0).add(title);
          }
        } else {
          List<List<String>> list = new ArrayList<>();
          List<String> list1 = new ArrayList<>();
          List<String> list2 = new ArrayList<>();
          list1.add(title);
          list.add(list1);
          list.add(list2);
          map.put(instructor[0], list);
        }
      } else {
        for (String s : instructor) {
          if (map.containsKey(s)) {
            boolean flag = false;
            for (String list : map.get(s).get(1)) {
              if (list.equals(title)) {
                flag = true;
                break;
              }
            }
            if (!flag) {
              map.get(s).get(1).add(title);
            }
          } else {
            List<List<String>> list = new ArrayList<>();
            List<String> list1 = new ArrayList<>();
            List<String> list2 = new ArrayList<>();
            list2.add(title);
            list.add(list1);
            list.add(list2);
            map.put(s, list);
          }
        }
      }
    }
    for (List<List<String>> lists : map.values()) {
      Collections.sort(lists.get(0));
      Collections.sort(lists.get(1));
    }

    return map;
  }

  public List<String> getCourses(int topK, String by) {

    // sort the courses in the desired order
    if (by.equals("hours")) {
      Collections.sort(
          courses,
          (o1, o2) -> {
            if (o1.getTotalCourseHours() == o2.getTotalCourseHours()) {
              return o1.title.compareTo(o2.title);
            } else {
              return o2.getTotalCourseHours() - o1.getTotalCourseHours();
            }
          });
    } else if (by.equals("participants")) {
      Collections.sort(
          courses,
          (o1, o2) -> {
            if (o1.participants == o2.participants) {
              return o1.title.compareTo(o2.title);
            } else {
              return o2.participants - o1.participants;
            }
          });
    }

    // create the sublist of topK courses
    List<String> list = new ArrayList<>();
    Set<String> set = new HashSet<>();
    for (int i = 0; i < courses.size(); i++) {
      if (set.add(courses.get(i).title)) {
        list.add(courses.get(i).title);
      }
      if (list.size() == topK) {
        break;
      }
    }
    return list;
  }

  public List<String> searchCourses(
      String courseSubject, double percentAudited, double totalCourseHours) {
    List<String> list = new ArrayList<>();
    for (Course course : courses) {
      if (course.subject.toLowerCase().contains(courseSubject.toLowerCase())
          && course.percentAudited >= percentAudited
          && course.getTotalCourseHours() <= totalCourseHours) {
        if (!list.contains(course.title)) {
          list.add(course.title);
        }
      }
    }
    Collections.sort(list);
    return list;
  }

  public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
    List<String> recommendedCourses = new ArrayList<>();
    Map<String, Double> similarityMap = new HashMap<>();
    Map<String, Date> launchDateMap = new HashMap<>();
    Map<String, Double> medianAgeMap = new HashMap<>();
    Map<String, Double> percentMaleMap = new HashMap<>();
    Map<String, Double> percentDegreeMap = new HashMap<>();
    Map<String, String> courseTitleMap = new HashMap<>();
    Map<String, Integer> courseCountMap = new HashMap<>();
    Map<String, String> newestcourse = new HashMap<>();
    for (Course course : courses) {
      String number = course.number;
      double medianAge = course.medianAge;
      double percentMale = course.percentMale;
      double percentDegree = course.percentDegree;
      Date launchDate = course.launchDate;
      String title = course.title;
      if (medianAgeMap.containsKey(number)) {
        double sumMedianAge = medianAgeMap.get(number);
        double sumPercentMale = percentMaleMap.get(number);
        double sumPercentDegree = percentDegreeMap.get(number);
        sumMedianAge += medianAge;
        sumPercentMale += (percentMale);
        sumPercentDegree += (percentDegree);
        courseCountMap.put(number, courseCountMap.get(number) + 1);
        medianAgeMap.put(number, sumMedianAge);
        percentMaleMap.put(number, sumPercentMale);
        percentDegreeMap.put(number, sumPercentDegree);
        if (launchDate.after(launchDateMap.get(number))) {
          launchDateMap.put(number, launchDate);
          newestcourse.put(number, title);
        }
      } else {
        medianAgeMap.put(number, medianAge);
        percentMaleMap.put(number, (percentMale));
        percentDegreeMap.put(number, (percentDegree));
        courseCountMap.put(number, 1);
        launchDateMap.put(number, launchDate);
        newestcourse.put(number, title);
      }
    }
    for (String number : medianAgeMap.keySet()) {
      double medianAge = medianAgeMap.get(number) / courseCountMap.get(number);
      double percentMale = percentMaleMap.get(number) / courseCountMap.get(number);
      double percentDegree = percentDegreeMap.get(number) / courseCountMap.get(number);
      double similarity =
          Math.pow(age - medianAge, 2)
              + Math.pow(gender * 100 - percentMale, 2)
              + Math.pow(isBachelorOrHigher * 100 - percentDegree, 2);
      similarityMap.put(number, similarity);
    }
    List<Map.Entry<String, Double>> list = new ArrayList<>(similarityMap.entrySet());
    list.sort(
        new Comparator<Map.Entry<String, Double>>() {
          @Override
          public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
            if (o1.getValue().equals(o2.getValue())) {
              return newestcourse.get(o1.getKey()).compareTo(newestcourse.get(o2.getKey()));
            }
            return o1.getValue().compareTo(o2.getValue());
          }
        });
    int count = 0;
    int count1 = 0;
    while (count1 < 10) {
      String number = list.get(count).getKey();
      count++;
      for (Course course : courses) {
        if (course.number.equals(number)) {
          if (!recommendedCourses.contains(newestcourse.get(number))) {
            recommendedCourses.add(newestcourse.get(number));
            count1++;
          }
        }
      }
    }
    return recommendedCourses;
  }
}

class Course {
  String institution;
  String number;
  Date launchDate;
  String title;
  String instructors;
  String subject;
  int year;
  int honorCode;
  int participants;
  int audited;
  int certified;
  double percentAudited;
  double percentCertified;
  double percentCertified50;
  double percentVideo;
  double percentForum;
  double gradeHigherZero;
  double totalHours;
  double medianHoursCertification;
  double medianAge;
  double percentMale;
  double percentFemale;
  double percentDegree;

  public Course(
      String institution,
      String number,
      Date launchDate,
      String title,
      String instructors,
      String subject,
      int year,
      int honorCode,
      int participants,
      int audited,
      int certified,
      double percentAudited,
      double percentCertified,
      double percentCertified50,
      double percentVideo,
      double percentForum,
      double gradeHigherZero,
      double totalHours,
      double medianHoursCertification,
      double medianAge,
      double percentMale,
      double percentFemale,
      double percentDegree) {
    this.institution = institution;
    this.number = number;
    this.launchDate = launchDate;
    if (title.startsWith("\"")) title = title.substring(1);
    if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
    this.title = title;
    if (instructors.startsWith("\"")) instructors = instructors.substring(1);
    if (instructors.endsWith("\""))
      instructors = instructors.substring(0, instructors.length() - 1);
    this.instructors = instructors;
    if (subject.startsWith("\"")) subject = subject.substring(1);
    if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
    this.subject = subject;
    this.year = year;
    this.honorCode = honorCode;
    this.participants = participants;
    this.audited = audited;
    this.certified = certified;
    this.percentAudited = percentAudited;
    this.percentCertified = percentCertified;
    this.percentCertified50 = percentCertified50;
    this.percentVideo = percentVideo;
    this.percentForum = percentForum;
    this.gradeHigherZero = gradeHigherZero;
    this.totalHours = totalHours;
    this.medianHoursCertification = medianHoursCertification;
    this.medianAge = medianAge;
    this.percentMale = percentMale;
    this.percentFemale = percentFemale;
    this.percentDegree = percentDegree;
  }

  public boolean isIndependently() {
    String[] numinstructors = instructors.split(", ");
    return numinstructors.length == 1;
  }

  public int getTotalCourseHours() {
    return (int) totalHours;
  }
}
