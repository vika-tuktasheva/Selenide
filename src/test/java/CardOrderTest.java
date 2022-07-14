import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CardOrderTest {

    public static String generateDate(int changeDay) {

        return LocalDate.now().plusDays(changeDay).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @BeforeEach
    public void setUp() {
        open("http://localhost:9999/");
    }

    @Test //тест с валидными значениями
    public void shouldSendForm() {
        open("http://localhost:9999/");
        $("[data-test-id=city] input").setValue("Санкт-Петербург");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate=generateDate(3);
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+78884545321");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        //проверка видимости уведомления
        $("[data-test-id=notification]").should(visible, Duration.ofSeconds(15));
        $("[class=notification__content]").should(text("Встреча успешно забронирована на " + planningDate));
    }

    @Test //ввод неверного города
    public void shouldSendFormWhenWrongCity() {
        open("http://localhost:9999");
        $("[data-test-id=city] input").setValue("Новокузнецк");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate=generateDate(3);
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+78884545321");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $$("[class=input__sub]").get(0).should(Condition.text("Доставка в выбранный город недоступна"), Duration.ofSeconds(15));
    }

    @Test //ввод неверной даты
    public void shouldSendFormWhenWrongDate() {
        open("http://localhost:9999");
        $("[data-test-id=city] input").setValue("Санкт-Петербург");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate=generateDate(3);
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id=date] input").setValue("01.05.2022");
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+78884545321");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $$("[class=input__sub]").get(1).should(text("Заказ на выбранную дату невозможен"), Duration.ofSeconds(15));
    }

    @Test //ввод неверного ФИО
    public void shouldSendFormWhenWrongName() {
        open("http://localhost:9999");
        $("[data-test-id=city] input").setValue("Санкт-Петербург");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate=generateDate(3);
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue("Иванов Иван15");
        $("[data-test-id=phone] input").setValue("+78884545321");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $$("[class=input__sub]").get(2).should(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."), Duration.ofSeconds(15));
    }

    @Test //ввод неверного телефона
    public void shouldSendFormWhenWrongPhone() {
        open("http://localhost:9999");
        $("[data-test-id=city] input").setValue("Санкт-Петербург");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate=generateDate(3);
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+7888454532156");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $$("[class=input__sub]").get(3).should(text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."), Duration.ofSeconds(15));
    }

    @Test //нет согласия
    public void shouldSendFormТoСonsent() {
        open("http://localhost:9999");
        $("[data-test-id=city] input").setValue("Санкт-Петербург");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate=generateDate(3);
        $("[data-test-id='date'] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+78884545321");
//        $("[data-test-id=agreement]").click();
        $(".button").click();
        $("[class=checkbox__text]").should(text("Я соглашаюсь с условиями обработки и использования моих персональных данных"), Duration.ofSeconds(15));
    }
}
