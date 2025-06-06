/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package deu.cse.spring_webmail.creator;

import deu.cse.spring_webmail.model.UserAdminAgent;
import jakarta.servlet.ServletContext;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author 박상현
 */
class AdminCreatorTest {
    
    private String server = "";
    private int port = 9997;
    private String cwd = "";
    private String rootId = "";
    private String rootPass = "";
    private String adminId = ""; 
    
    @Mock
    private ServletContext ctx;
    
    @InjectMocks
    private AdminCreator creator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        creator = new AdminCreator();
    }
    
    /**
     * Test of creatAdminAgent method, of class AdminCreator.
     */
    @Test
    void testCreatAdminAgent() {
        System.out.println("creatAdminAgent");
        UserAdminAgent adminAgent  = creator.creatAdminAgent(server, port, cwd, rootId, rootPass, adminId);
        assertNotNull(adminAgent, "useradminagent 객체 생설 실패");
    }
    
}
