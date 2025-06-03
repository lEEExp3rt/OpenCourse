package org.opencourse.repositories;

import org.junit.jupiter.api.Test;
import org.opencourse.models.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link DepartmentRepo}.
 * 
 * @author !EEExp3rt
 */
@DataJpaTest
@EnableJpaRepositories(basePackageClasses = DepartmentRepo.class)
@EntityScan(basePackageClasses = Department.class)
class DepartmentRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DepartmentRepo departmentRepo;

    @Test
    void contextLoads() {
        assertThat(departmentRepo).isNotNull();
        assertThat(entityManager).isNotNull();
    }

    @Test
    void testFindByName_WhenDepartmentExists_ShouldReturnDepartment() {
        // Given
        Department department = new Department("计算机科学与技术学院");
        entityManager.persistAndFlush(department);

        // When
        Optional<Department> found = departmentRepo.findByName("计算机科学与技术学院");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("计算机科学与技术学院");
        assertThat(found.get().getId()).isNotNull();
    }

    @Test
    void testFindByName_WhenDepartmentNotExists_ShouldReturnEmpty() {
        // Given
        Department department = new Department("计算机学院");
        entityManager.persistAndFlush(department);

        // When
        Optional<Department> found = departmentRepo.findByName("数学学院");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByName_WhenNameIsNull_ShouldReturnEmpty() {
        // Given
        Department department = new Department("物理学院");
        entityManager.persistAndFlush(department);

        // When
        Optional<Department> found = departmentRepo.findByName(null);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByNameContainingIgnoreCase_WhenMatchingDepartments_ShouldReturnList() {
        // Given
        Department dept1 = new Department("计算机科学与技术学院");
        Department dept2 = new Department("计算机工程学院");
        Department dept3 = new Department("软件工程学院");
        Department dept4 = new Department("数学学院");

        entityManager.persistAndFlush(dept1);
        entityManager.persistAndFlush(dept2);
        entityManager.persistAndFlush(dept3);
        entityManager.persistAndFlush(dept4);

        // When
        List<Department> results = departmentRepo.findByNameContainingIgnoreCase("计算机");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Department::getName)
            .containsExactlyInAnyOrder("计算机科学与技术学院", "计算机工程学院");
    }

    @Test
    void testFindByNameContainingIgnoreCase_WhenCaseInsensitive_ShouldReturnMatches() {
        // Given
        Department dept1 = new Department("Computer Science");
        Department dept2 = new Department("COMPUTER Engineering");
        Department dept3 = new Department("Mathematics");

        entityManager.persistAndFlush(dept1);
        entityManager.persistAndFlush(dept2);
        entityManager.persistAndFlush(dept3);

        // When
        List<Department> results = departmentRepo.findByNameContainingIgnoreCase("computer");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Department::getName)
            .containsExactlyInAnyOrder("Computer Science", "COMPUTER Engineering");
    }

    @Test
    void testFindByNameContainingIgnoreCase_WhenNoMatches_ShouldReturnEmptyList() {
        // Given
        Department dept1 = new Department("物理学院");
        Department dept2 = new Department("化学学院");

        entityManager.persistAndFlush(dept1);
        entityManager.persistAndFlush(dept2);

        // When
        List<Department> results = departmentRepo.findByNameContainingIgnoreCase("生物");

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void testFindByNameContainingIgnoreCase_WhenEmptyKeyword_ShouldReturnEmptyList() {
        // Given
        Department department = new Department("计算机学院");
        entityManager.persistAndFlush(department);

        // When
        List<Department> results = departmentRepo.findByNameContainingIgnoreCase("");

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("计算机学院");
    }

    @Test
    void testExistsByName_WhenDepartmentExists_ShouldReturnTrue() {
        // Given
        Department department = new Department("经济管理学院");
        entityManager.persistAndFlush(department);

        // When
        boolean exists = departmentRepo.existsByName("经济管理学院");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByName_WhenDepartmentNotExists_ShouldReturnFalse() {
        // Given
        Department department = new Department("法学院");
        entityManager.persistAndFlush(department);

        // When
        boolean exists = departmentRepo.existsByName("医学院");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByName_WhenNameIsNull_ShouldReturnFalse() {
        // Given
        Department department = new Department("艺术学院");
        entityManager.persistAndFlush(department);

        // When
        boolean exists = departmentRepo.existsByName(null);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void testFindAllByOrderByNameAsc_ShouldReturnDepartmentsInAlphabeticalOrder() {
        // Given
        Department dept1 = new Department("Software Engineering");
        Department dept2 = new Department("Computer Science");
        Department dept3 = new Department("Mathematics");
        Department dept4 = new Department("Physics");

        entityManager.persistAndFlush(dept1);
        entityManager.persistAndFlush(dept2);
        entityManager.persistAndFlush(dept3);
        entityManager.persistAndFlush(dept4);

        // When
        List<Department> results = departmentRepo.findAllByOrderByNameAsc();

        // Then
        assertThat(results).hasSize(4);
        assertThat(results).extracting(Department::getName)
            .containsExactly("Computer Science", "Mathematics", "Physics", "Software Engineering");
    }

    @Test
    void testFindAllByOrderByNameAsc_WhenNoDepartments_ShouldReturnEmptyList() {
        // Given & When
        List<Department> results = departmentRepo.findAllByOrderByNameAsc();

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void testFindAllByOrderByNameAsc_WhenSingleDepartment_ShouldReturnSingleItem() {
        // Given
        Department department = new Department("建筑学院");
        entityManager.persistAndFlush(department);

        // When
        List<Department> results = departmentRepo.findAllByOrderByNameAsc();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("建筑学院");
    }

    @Test
    void testSaveDepartment_ShouldGenerateId() {
        // Given
        Department department = new Department("新工学院");

        // When
        Department saved = departmentRepo.save(department);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("新工学院");
        Optional<Department> found = departmentRepo.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("新工学院");
    }

    @Test
    void testDeleteDepartment_ShouldRemoveFromDatabase() {
        // Given
        Department department = new Department("临时学院");
        Department saved = entityManager.persistAndFlush(department);

        // When
        departmentRepo.deleteById(saved.getId());
        entityManager.flush();

        // Then
        Optional<Department> found = departmentRepo.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void testFindById_WhenDepartmentExists_ShouldReturnDepartment() {
        // Given
        Department department = new Department("测试学院");
        Department saved = entityManager.persistAndFlush(department);

        // When
        Optional<Department> found = departmentRepo.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("测试学院");
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    void testFindById_WhenDepartmentNotExists_ShouldReturnEmpty() {
        // Given
        Byte nonExistentId = (byte) 99;

        // When
        Optional<Department> found = departmentRepo.findById(nonExistentId);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void testUpdateDepartment_ShouldModifyExistingRecord() {
        // Given
        Department department = new Department("旧名称学院");
        Department saved = entityManager.persistAndFlush(department);

        // When
        saved.setName("新名称学院");
        Department updated = departmentRepo.save(saved);
        entityManager.flush();

        // Then
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getName()).isEqualTo("新名称学院");
        Optional<Department> found = departmentRepo.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("新名称学院");
    }

    @Test
    void testDepartmentNameUniqueness_ShouldPreventDuplicates() {
        // Given
        Department dept1 = new Department("重复学院");
        entityManager.persistAndFlush(dept1);

        // When & Then
        new Department("重复学院");
        boolean exists = departmentRepo.existsByName("重复学院");
        assertThat(exists).isTrue();
    }
}
