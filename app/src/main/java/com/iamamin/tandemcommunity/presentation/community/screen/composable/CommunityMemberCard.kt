package com.iamamin.tandemcommunity.presentation.community.screen.composable

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.iamamin.tandemcommunity.R
import com.iamamin.tandemcommunity.domain.model.CommunityMember
import com.iamamin.tandemcommunity.presentation.theme.AppTypography
import com.iamamin.tandemcommunity.presentation.utils.ThemedPreviewWrapper

@Composable
fun CommunityMemberCard(
    member: CommunityMember,
    onLikeClicked: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = member.pictureUrl,
                contentDescription = stringResource(R.string.member_photo_description, member.firstname),
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )


            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = member.firstname,
                        style = AppTypography.titleMedium,
                    )
                    if (member.isNew) {
                        Text(
                            text = stringResource(R.string.badge_new),
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF7BC8A4), shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            color = Color.White,
                            style = AppTypography.labelSmall,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Topic
                Text(
                    text = member.topic,
                    style = AppTypography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Languages + Like button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        LanguageLabel(label = stringResource(R.string.language_native), language = member.native)
                        LanguageLabel(label = stringResource(R.string.language_learns), language = member.learn)
                    }

                    IconButton(onClick = onLikeClicked, modifier = Modifier.size(32.dp)) {
                        Text(
                            text = if (member.isLiked) "\uD83D\uDC4D" else "\uD83D\uDC4D\u200D",
                            fontSize = 18.sp,
                            // liked = colored thumbs up, default = outline
                            modifier = if (!member.isLiked) Modifier.alpha(0.3f) else Modifier
                        )
                    }
                }
            }
        }
    }
}


private class CommunityMemberCardPreviewPreviewParameterProvider :
    PreviewParameterProvider<CommunityMember> {
    override val values: Sequence<CommunityMember>
        get() = sequenceOf(
            CommunityMember(
                id = 1,
                firstname = "Amin",
                topic = "I want to learn Android development and share my knowledge about Kotlin.",
                pictureUrl = "https://tandem2019.web.app/img/pic1.png",
                native = "Persian",
                learn = "German",
                isNew = true,
                isLiked = false
            ), CommunityMember(
                id = 2,
                firstname = "John",
                topic = "Looking for a language partner to practice Spanish.",
                pictureUrl = "https://tandem2019.web.app/img/pic2.png",
                native = "English",
                learn = "Spanish",
                isNew = false,
                isLiked = true
            )
        )
}
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Preview(showBackground = true)
@Composable
@PreviewWrapper(ThemedPreviewWrapper::class)
private fun CommunityMemberCardPreview(
    @PreviewParameter(CommunityMemberCardPreviewPreviewParameterProvider::class) communityMember: CommunityMember
) {
    CommunityMemberCard(
        member = communityMember,
        onLikeClicked = {}
    )
}
