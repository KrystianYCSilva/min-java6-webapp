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
 * Exemplo de teste E2E com Selenium para fluxo principal da aplicacao ZK.
 */
@Ignore("Teste E2E depende de servidor Tomcat ativo e webdriver instalado.")
public class CensoE2ETest {

    private WebDriver driver;

    @Test
    public void deveExecutarFluxoCompletoDoAlunoNoFrontendZk() throws Exception {
        String baseUrl = System.getProperty("e2e.baseUrl", "http://localhost:8080/censo-superior-2025");
        driver = new FirefoxDriver();

        driver.get(baseUrl + "/login.zul");
        waitForElement(By.id("txtLogin"), 10).sendKeys("admin");
        waitForElement(By.id("txtSenha"), 10).sendKeys("admin123");
        waitForElement(By.id("btnEntrar"), 10).click();
        waitForUrlContains("/app/menu.zul?view=dashboard", 10);

        waitForElement(By.id("btnNavAluno"), 10).click();
        waitForUrlContains("/app/menu.zul?view=aluno-list", 10);

        waitForElement(By.id("btnNovoListAluno"), 10).click();
        waitForUrlContains("sub=aluno-form", 10);

        waitForElement(By.id("txtIdAlunoInep"), 10).sendKeys("77001");
        waitForElement(By.id("txtNomeAluno"), 10).sendKeys("Aluno E2E Exemplo");
        waitForElement(By.id("txtCpfAluno"), 10).sendKeys("98765432100");
        fillDatebox("dtNascimentoAluno", "2001-06-10");
        waitForElement(By.id("txtUfNascimentoAluno"), 10).sendKeys("PR");
        waitForElement(By.id("txtMunicipioNascimentoAluno"), 10).sendKeys("Maringa");

        waitForElement(By.id("btnSalvarFormAluno"), 10).click();
        waitForUrlContains("/app/menu.zul?view=aluno-list", 10);

        String pageText = driver.getPageSource();
        Assert.assertTrue(pageText.contains("Aluno incluido com sucesso."));
    }

    private WebElement waitForElement(By locator, int timeoutSeconds) throws Exception {
        long timeout = System.currentTimeMillis() + (timeoutSeconds * 1000L);
        RuntimeException lastError = null;

        while (System.currentTimeMillis() < timeout) {
            try {
                return driver.findElement(locator);
            } catch (RuntimeException ex) {
                lastError = ex;
                Thread.sleep(200L);
            }
        }

        if (lastError != null) {
            throw lastError;
        }
        throw new IllegalStateException("Elemento nao encontrado: " + locator);
    }

    private void fillDatebox(String componentId, String value) throws Exception {
        try {
            WebElement field = waitForElement(By.id(componentId), 3);
            field.clear();
            field.sendKeys(value);
            return;
        } catch (RuntimeException ignored) {
            // Fallback para o input real de Datebox no DOM do ZK.
        }

        WebElement realField = waitForElement(By.id(componentId + "-real"), 10);
        realField.clear();
        realField.sendKeys(value);
    }

    private void waitForUrlContains(String expectedFragment, int timeoutSeconds) throws Exception {
        long timeout = System.currentTimeMillis() + (timeoutSeconds * 1000L);

        while (System.currentTimeMillis() < timeout) {
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl != null && currentUrl.indexOf(expectedFragment) >= 0) {
                return;
            }
            Thread.sleep(200L);
        }

        Assert.fail("URL esperada nao encontrada. Esperado fragmento: " + expectedFragment
                + ". URL atual: " + driver.getCurrentUrl());
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
