package gdsc.konkuk.platformcore.external.discord;

import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.DISCORD_ERROR_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gdsc.konkuk.platformcore.application.email.exceptions.EmailErrorCode;
import gdsc.konkuk.platformcore.external.email.exceptions.EmailSendingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DiscordMessageTest {

    @Test
    @DisplayName("디스코드 에러메세지 생성 시 서버 로그 메세지를 출력")
    void should_create_discord_error_message_with_server_log_message() {
        // given
        Exception occurredException = EmailSendingException.of(EmailErrorCode.EMAIL_NOT_FOUND);

        // when
        DiscordMessage discordMessage = DiscordMessage.of(occurredException);

        // then
        assertEquals(DISCORD_ERROR_TITLE, discordMessage.getContent());
        assertEquals(occurredException.getMessage(), discordMessage.getEmbeds().get(0).getTitle());
    }

}
