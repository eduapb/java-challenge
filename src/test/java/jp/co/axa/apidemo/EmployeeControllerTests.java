package jp.co.axa.apidemo;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.EmployeeService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeControllerTests {

	@Autowired
	private WebApplicationContext webApplicationContext;
	@MockBean
	private EmployeeService employeeService;

	List<Employee> listEmployees;
	// @Autowired
	MockMvc mockMvc;

	/* Helper function to do create Employee objects */
	public static Employee createEmployee(int id, String name, int salary, String department) {
		Employee employee = new Employee();
		Field[] fields = employee.getClass().getDeclaredFields();
		for (Field field : fields)	field.setAccessible(true);
		try {
			fields[0].set(employee, Long.valueOf(id));
			fields[1].set(employee, name);
			fields[2].set(employee, salary);
			fields[3].set(employee, department);	
		} catch (Exception e) {
			return new Employee();
		}
		return employee;
	}

	/* Helper function to do comparisons between Employee objects */
	public static void compareEmployeesExceptId(Employee expected, Employee actual) {
		Field[] fields = expected.getClass().getDeclaredFields();
		for (Field field : fields)	{
			field.setAccessible(true);
			try {
				Object obj1 = field.get(actual);
				Object obj2 = field.get(expected);
				assertEquals(field.getName().concat(" field values should match."), obj2, obj1);
			} catch (Exception e) {
				assertTrue(field.getName().concat(" field values could not be checked."), false);
			}
		}
	}

	@Before
	public void setup() {
		listEmployees = new ArrayList<Employee>();
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		Employee employee1 = createEmployee(1, "Joe Poe", 10000, "HR");
		Employee employee2 = createEmployee(2, "Allison Smith", 15000, "DEVELOPMENT");
		listEmployees.add(employee1);
		listEmployees.add(employee2);
		Mockito.when(employeeService.retrieveEmployees()).thenReturn(listEmployees);
		Mockito.when(employeeService.getEmployee(Long.valueOf(1))).thenReturn(employee1);
		Mockito.when(employeeService.getEmployee(Long.valueOf(2))).thenReturn(employee2);
	}

	@Test
	public void getEmployees_returnsArrayOfEmployeeObjects() throws Exception {
		// We verify the status, format and contents of the response.
		mockMvc.perform(get("/api/v1/employees"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$[0].id").value(1))
			.andExpect(jsonPath("$[0].name").value("Joe Poe"))
			.andExpect(jsonPath("$[0].salary").value(10000))
			.andExpect(jsonPath("$[0].department").value("HR"))
			.andExpect(jsonPath("$[1].id").value(2))
			.andExpect(jsonPath("$[1].name").value("Allison Smith"))
			.andExpect(jsonPath("$[1].salary").value(15000))
			.andExpect(jsonPath("$[1].department").value("DEVELOPMENT"));
	}

	@Test
	public void getEmployee_shouldReturnRequestedEmployee() throws Exception {
		// We verify the status, format and contents of the response.
		mockMvc.perform(get("/api/v1/employees/2"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.id").value(2))
			.andExpect(jsonPath("$.name").value("Allison Smith"))
			.andExpect(jsonPath("$.salary").value(15000))
			.andExpect(jsonPath("$.department").value("DEVELOPMENT"));
	}

	@Test
	public void saveEmployee_shouldCallSaveEmployee() throws Exception {
		// We verify the status, format of the response and number of calls to the Service.
		mockMvc.perform(
			post("/api/v1/employees")
				.contentType("application/json;charset=UTF-8")
				.content("{	\"id\": 4, \"name\": \"Bill\", \"salary\": \"10000\", \"department\": \"HR\"}") 
				.accept("application/json;charset=UTF-8")
			)
			.andExpect(status().isOk())
			.andDo(print());
		
		// Employee creation is handled by the service
		ArgumentCaptor<Employee> argument = ArgumentCaptor.forClass(Employee.class);
		Mockito.verify(employeeService, times(1))
			.saveEmployee(argument.capture());
		Employee expected = createEmployee(4, "Bill", 10000, "HR");
		compareEmployeesExceptId(expected, argument.getValue());
	}

	@Test
	public void updateEmployee_shouldCallUpdateEmployee() throws Exception {
		// We verify the status, format of the response and number of calls to the Service.
		mockMvc.perform(
			put("/api/v1/employees/1")
				.contentType("application/json;charset=UTF-8")
				.content("{	\"id\": 1, \"name\": \"Bill\", \"salary\": \"10000\", \"department\": \"HR\"}") 
				.accept("application/json;charset=UTF-8")
			)
			.andExpect(status().isOk());
		
		// Employee exists in DB so updateEmployee should be called
		ArgumentCaptor<Employee> argument = ArgumentCaptor.forClass(Employee.class);
		Mockito.verify(employeeService, times(1))
			.getEmployee(Long.valueOf(1));
		Mockito.verify(employeeService, times(1))
			.updateEmployee(argument.capture());
		Employee expected = createEmployee(1, "Bill", 10000, "HR");
		compareEmployeesExceptId(expected, argument.getValue());
	}

	@Test
	public void updateEmployee_shouldNotCallUpdateEmployee() throws Exception {
		// We verify the status, format of the response and number of calls to the Service.
		mockMvc.perform(
			put("/api/v1/employees/3")
				.contentType("application/json;charset=UTF-8")
				.content("{	\"id\": 3, \"name\": \"Bill\", \"salary\": \"10000\", \"department\": \"HR\"}") 
				.accept("application/json;charset=UTF-8")
			)
			.andExpect(status().isOk());

		// Employee does not exist in DB so updateEmployee should not be called
		Mockito.verify(employeeService, times(1))
			.getEmployee(Long.valueOf(3));
		Mockito.verify(employeeService, times(0))
			.updateEmployee(Mockito.any(Employee.class));
	}
}
