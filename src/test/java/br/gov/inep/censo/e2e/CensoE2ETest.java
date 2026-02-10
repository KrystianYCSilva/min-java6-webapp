package br.gov.inep.censo.e2e;

import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Exemplo de teste E2E com Selenium para fluxo principal da aplicacao.
 */
@Ignore("Teste E2E depende de servidor Tomcat ativo e webdriver instalado.")
public class CensoE2ETest {

    private WebDriver driver;

    @Test
    public void deveExecutarFluxoCompletoDoAluno() throws Exception {
        String baseUrl = System.getProperty("e2e.baseUrl", "http://localhost:8080/censo-superior-2025");
        driver = new FirefoxDriver();

        driver.get(baseUrl + "/login");
        driver.findElement(By.id("login")).sendKeys("admin");
        driver.findElement(By.id("senha")).sendKeys("admin123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        driver.findElement(By.linkText("Abrir Modulo Aluno")).click();

        driver.findElement(By.id("idAlunoInep")).sendKeys("77001");
        driver.findElement(By.id("nome")).sendKeys("Aluno E2E Exemplo");
        driver.findElement(By.id("cpf")).sendKeys("98765432100");
        driver.findElement(By.id("dataNascimento")).sendKeys("2001-06-10");
        driver.findElement(By.id("ufNascimento")).sendKeys("PR");
        driver.findElement(By.id("municipioNascimento")).sendKeys("Maringa");

        WebElement submit = driver.findElement(By.cssSelector("button[type='submit']"));
        submit.click();

        driver.get(baseUrl + "/home.jsp");
        String pageText = driver.getPageSource();
        Assert.assertTrue(pageText.contains("Cadastro de aluno realizado com sucesso."));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
