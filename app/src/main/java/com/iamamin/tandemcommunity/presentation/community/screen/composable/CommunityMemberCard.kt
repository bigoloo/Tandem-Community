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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper
import coil3.compose.AsyncImage
import com.iamamin.tandemcommunity.R
import com.iamamin.tandemcommunity.domain.model.CommunityMember
import com.iamamin.tandemcommunity.presentation.theme.Dimens
import com.iamamin.tandemcommunity.presentation.theme.Spacing
import com.iamamin.tandemcommunity.presentation.utils.ThemedPreviewWrapper

@Composable
fun CommunityMemberCard(
    member: CommunityMember,
    onLikeClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            AsyncImage(
                model = member.pictureUrl,
                contentDescription = stringResource(
                    R.string.member_photo_description,
                    member.firstname
                ),
                modifier = Modifier
                    .size(Dimens.avatarSize)
                    .clip(RoundedCornerShape(Dimens.avatarCornerRadius)),
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
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (member.isNew) {
                        Text(
                            text = stringResource(R.string.badge_new),
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = RoundedCornerShape(Dimens.badgeCornerRadius)
                                )
                                .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                Text(
                    text = member.topic,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                        LanguageLabel(
                            label = stringResource(R.string.language_native),
                            language = member.native
                        )
                        LanguageLabel(
                            label = stringResource(R.string.language_learns),
                            language = member.learn
                        )
                    }

                    val likeScale by animateFloatAsState(
                        targetValue = if (member.isLiked) 1f else 0.85f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "likeScale"
                    )
                    val likeTint by animateColorAsState(
                        targetValue = if (member.isLiked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        label = "likeTint"
                    )

                    IconButton(
                        onClick = onLikeClicked,
                        modifier = Modifier.size(Dimens.likeButtonSize)
                    ) {
                        Icon(
                            imageVector = if (member.isLiked) {
                                Icons.Default.ThumbUp
                            } else {
                                Icons.Outlined.ThumbUp
                            },
                            contentDescription = stringResource(
                                if (member.isLiked) R.string.action_unlike else R.string.action_like
                            ),
                            tint = likeTint,
                            modifier = Modifier
                                .size(Dimens.likeIconSize)
                                .scale(likeScale)
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
    name = "DefaultPreviewDark",
    apiLevel = 34
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight",
    apiLevel = 34
)
@Preview(showBackground = true, apiLevel = 34)
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
