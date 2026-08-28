package com.upisoundbox

import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.RawNotification
import com.upisoundbox.notification.NotificationNormalizer
import com.upisoundbox.parser.GooglePayParser
import com.upisoundbox.parser.ParseResult
import com.upisoundbox.speech.AnnouncementFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExactNotificationPipelineVerificationTest {

    @Test
    fun testExactGooglePayRealDevicePayload() {
        val raw = RawNotification(
            packageName = "com.google.android.apps.nbu.paisa.user",
            notificationKey = "0|com.google.android.apps.nbu.paisa.user|1|null|10562",
            postedAt = System.currentTimeMillis(),
            title = "AFZAL KASAM MANSURI paid you ₹1.00",
            text = "Tap to view."
        )

        val normalized = NotificationNormalizer.normalize(raw)
        val parser = GooglePayParser()
        val result = parser.parse(normalized)

        assertTrue(result is ParseResult.Success)
        val event = (result as ParseResult.Success).event

        assertEquals(Provider.GOOGLE_PAY, event.provider)
        assertEquals(Direction.CREDIT, event.direction)
        assertEquals(100L, event.amountMinor)
        assertEquals("AFZAL KASAM MANSURI", event.payerName)

        val englishAnnouncement = AnnouncementFormatter.format(event, "en", includePayer = true, includeProvider = true)
        assertEquals("Received one rupee from AFZAL KASAM MANSURI on Google Pay.", englishAnnouncement)

        val hindiAnnouncement = AnnouncementFormatter.format(event, "hi", includePayer = true, includeProvider = true)
        assertEquals("Google Pay पर AFZAL KASAM MANSURI से एक रुपये प्राप्त हुए।", hindiAnnouncement)
    }
}
