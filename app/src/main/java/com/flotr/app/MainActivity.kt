package com.flotr.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF0B0D16)
private val SurfaceDark = Color(0xFF171A29)
private val Purple = Color(0xFF8B5CF6)
private val Mint = Color(0xFF54E1B8)
private val Muted = Color(0xFF9CA3B8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FlotrApp() }
    }
}

private enum class Tab(val title: String) {
    HOME("الرئيسية"), WALLET("المحفظة"), INVITE("الإحالات"), PROFILE("حسابي")
}

@Composable
private fun FlotrApp() {
    var selected by remember { mutableStateOf(Tab.HOME) }
    var demoPoints by remember { mutableIntStateOf(1250) }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Purple,
            secondary = Mint,
            background = Background,
            surface = SurfaceDark
        )
    ) {
        Scaffold(
            containerColor = Background,
            bottomBar = {
                NavigationBar(containerColor = SurfaceDark) {
                    listOf(
                        Tab.HOME to Icons.Default.Home,
                        Tab.WALLET to Icons.Default.Wallet,
                        Tab.INVITE to Icons.Default.People,
                        Tab.PROFILE to Icons.Default.AccountCircle
                    ).forEach { (tab, icon) ->
                        NavigationBarItem(
                            selected = selected == tab,
                            onClick = { selected = tab },
                            icon = { Icon(icon, contentDescription = tab.title) },
                            label = { Text(tab.title, fontSize = 10.sp) }
                        )
                    }
                }
            }
        ) { padding ->
            when (selected) {
                Tab.HOME -> HomeScreen(demoPoints, padding) { demoPoints += 10 }
                Tab.WALLET -> WalletScreen(demoPoints, padding)
                Tab.INVITE -> InviteScreen(padding)
                Tab.PROFILE -> ProfileScreen(padding)
            }
        }
    }
}

@Composable
private fun HomeScreen(points: Int, padding: PaddingValues, onDemoCollect: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 26.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("مرحباً بك في", color = Muted, fontSize = 14.sp)
                    Text("FLOTR", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                }
                Box(
                    Modifier.size(46.dp).background(SurfaceDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text("F", color = Purple, fontSize = 24.sp, fontWeight = FontWeight.Black) }
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier.background(
                        Brush.linearGradient(listOf(Color(0xFF6339C9), Color(0xFF25204D), Color(0xFF171A29)))
                    ).padding(22.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("إجمالي نقاطك", color = Color(0xFFE5DDFE))
                        Text("%,d".format(points), color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                        Text("نقطة", color = Color(0xFFD7CCFF))
                        HorizontalDivider(color = Color.White.copy(alpha = .18f))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("رصيد تجريبي", color = Color.White.copy(alpha = .8f))
                            Text("غير قابل للسحب حالياً", color = Color.White.copy(alpha = .8f), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
        item {
            SectionTitle("المكافأة اليومية", "اجمع نقاطك بانتظام")
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CardGiftcard, null, tint = Mint, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("العداد اليومي", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("المكافأة التجريبية: 10 نقاط", color = Muted, fontSize = 12.sp)
                        }
                    }
                    Text("24:00:00", color = Mint, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Text("واجهة أولية — ربط العداد بالخادم مطلوب قبل منح نقاط حقيقية.", color = Muted, fontSize = 11.sp)
                    Button(
                        onClick = onDemoCollect,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("تجربة إضافة 10 نقاط") }
                }
            }
        }
        item {
            SectionTitle("اختصارات", "كل ما تحتاجه في مكان واحد")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickCard("المحفظة", "سجل النقاط", Modifier.weight(1f))
                QuickCard("ادعُ صديقاً", "اكسب معاً", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Muted, fontSize = 12.sp)
    }
}

@Composable
private fun QuickCard(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text(subtitle, color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun WalletScreen(points: Int, padding: PaddingValues) {
    Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("المحفظة", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(22.dp)) {
            Column(Modifier.padding(22.dp)) {
                Text("رصيد النقاط", color = Muted)
                Text("%,d نقطة".format(points), color = Mint, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text("هذه بيانات تجريبية محلية وليست رصيداً مالياً.", color = Muted, fontSize = 11.sp)
            }
        }
        Text("سجل العمليات", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("لا توجد عمليات محفوظة بعد.", color = Muted)
    }
}

@Composable
private fun InviteScreen(padding: PaddingValues) {
    Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("الإحالات", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("ادعُ أصدقاءك للانضمام إلى FLOTR.", color = Muted)
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(22.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("كود الدعوة الخاص بك", color = Muted)
                Text("FLOTR-XXXXXX", color = Mint, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("سيتم إنشاء كود فريد لكل مستخدم بعد ربط تسجيل الدخول بالخادم.", color = Muted, fontSize = 12.sp)
                OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("مشاركة الدعوة قريباً") }
            }
        }
    }
}

@Composable
private fun ProfileScreen(padding: PaddingValues) {
    Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("حسابي", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        QuickCard("مستخدم تجريبي", "لم يتم إعداد تسجيل الدخول بعد.", Modifier.fillMaxWidth())
        QuickCard("الإعدادات", "اللغة • الإشعارات • الخصوصية", Modifier.fillMaxWidth())
        QuickCard("حول FLOTR", "الإصدار 1.0.0 — نسخة أولية", Modifier.fillMaxWidth())
    }
}
