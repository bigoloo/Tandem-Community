package com.iamamin.tandemcommunity.presentation.community.screen.composable

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iamamin.tandemcommunity.domain.model.CommunityMember
import com.iamamin.tandemcommunity.presentation.theme.AppTypography
import com.iamamin.tandemcommunity.presentation.theme.TandemCommunityTheme

@Composable
fun CommunityMemberCard(
    member: CommunityMember,
    onLikeClicked: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /*   // Profile picture
               AsyncImage(
                   model = user.pictureUrl,
                   contentDescription = "${user.firstname}'s photo",
                   modifier = Modifier
                       .size(80.dp)
                       .clip(RoundedCornerShape(8.dp)),
                   contentScale = ContentScale.Crop
               )*/

            Column(modifier = Modifier.weight(1f)) {
                // Name + reference count / NEW badge
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
                            text = "NEW",
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF7BC8A4),
                                    shape = RoundedCornerShape(4.dp)
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
                        LanguageLabel(label = "NATIVE", languages = member.natives)
                        LanguageLabel(label = "LEARNS", languages = member.learns)
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


@Preview
@Composable
fun CommunityMemberCardPreview() {
    TandemCommunityTheme {

    }
}
