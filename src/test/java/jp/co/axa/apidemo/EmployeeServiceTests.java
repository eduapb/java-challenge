package jp.co.axa.apidemo;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import jp.co.axa.apidemo.services.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeServiceTests {

    @Autowired
    private EmployeeService employeeService;

    @MockBean
    private EmployeeRepository employeeRepository;

	@Before
	public void setUp() {
		Employee employee1 = createEmployee(1, "Joe Poe", 10000, "HR");
		Employee employee2 = createEmployee(2, "Allison Smith", 15000, "DEVELOPMENT");
		Optional<Employee> mockedFindById2 = Optional.of(employee2);
		List<Employee> mockListEmployees = new ArrayList<Employee>();
		mockListEmployees.add(employee1);
		mockListEmployees.add(employee2);
		Mockito.when(employeeRepository.findAll())
			.thenReturn(mockListEmployees);
		Mockito.when(employeeRepository.findById(Long.valueOf(2)))
			.thenReturn(mockedFindById2);
		Mockito.when(employeeRepository.findById(Long.valueOf(2)))
			.thenReturn(mockedFindById2);
	}

	/* Helper function to do comparisons between Employee objects */
	public static void compareEmployeesExceptId(Employee expected, Employee actual) {
		Field[] fields = expected.getClass().getDeclaredFields();
		for (Field field : fields)	{
			// if (!field.getName().equals("id")) {
				field.setAccessible(true);
				try {
					Object obj1 = field.get(actual);
					Object obj2 = field.get(expected);
					assertEquals(field.getName().concat(" field values should match."), obj2, obj1);
				} catch (Exception e) {
					assertTrue(field.getName().concat(" field values could not be checked."), false);
				}
			// }
		}
	}

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

	@Test
	public void retrieveEmployees_shouldGetAllEmployees() {
		List<Employee> actual = employeeService.retrieveEmployees();
		Employee expected1 = createEmployee(1, "Joe Poe", 10000, "HR");
		Employee expected2 = createEmployee(2, "Allison Smith", 15000, "DEVELOPMENT");
		compareEmployeesExceptId(expected1, actual.get(0));
		compareEmployeesExceptId(expected2, actual.get(1));
	}

	@Test
	public void getEmployee_shouldGetRequestedEmployee() {
		Employee actual = employeeService.getEmployee(Long.valueOf(2));
		Employee expected = createEmployee(2, "Allison Smith", 15000, "DEVELOPMENT");
		compareEmployeesExceptId(expected, actual);
	}

	@Test
	public void saveEmployee_shouldCallSave() {
		Employee employee = createEmployee(3, "Bill Collins", 18000, "SALES");
		employeeService.saveEmployee(employee);
		Mockito.verify(employeeRepository, times(1)).save(employee);
	}

	@Test
	public void deleteEmployee_shouldCallDeleteById() {
		employeeService.deleteEmployee(Long.valueOf(3));
		Mockito.verify(employeeRepository, times(1)).deleteById(Long.valueOf(3));
	}

	@Test
	public void updateEmployee_shouldCallSave() {
		Employee employee = createEmployee(3, "Bill Collins2", 8000, "SALES2");
		employeeService.updateEmployee(employee);
		Mockito.verify(employeeRepository, times(1)).save(employee);
	}
}
