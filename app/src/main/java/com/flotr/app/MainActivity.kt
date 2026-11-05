package com.flotr.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg=Color(0xFF0B0D16); private val Panel=Color(0xFF171A29)
private val Purple=Color(0xFF8B5CF6); private val Mint=Color(0xFF54E1B8); private val Muted=Color(0xFF9CA3B8)
private const val MIN_POINTS=1000L
class MainActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{FlotrApp()}}}
private enum class Screen{HOME,WALLET,ACCOUNT,ADMIN}
private data class Withdrawal(val id:String,val points:Long,val last4:String,var status:String="قيد المراجعة")
@Composable private fun FlotrApp(){
 var screen by rememberSaveable{mutableStateOf(Screen.HOME)}
 var points by rememberSaveable{mutableLongStateOf(1250L)}
 var claimed by rememberSaveable{mutableStateOf(false)}
 var admin by rememberSaveable{mutableStateOf(false)}
 val requests= remember{mutableStateListOf<Withdrawal>()}
 val reserved=requests.filter{it.status in listOf("قيد المراجعة","تمت الموافقة","قيد التحويل")}.sumOf{it.points}
 val available=(points-reserved).coerceAtLeast(0)
 MaterialTheme(colorScheme=darkColorScheme(primary=Purple,secondary=Mint,background=Bg,surface=Panel)){
 Scaffold(containerColor=Bg,bottomBar={NavigationBar(containerColor=Panel){
 NavigationBarItem(screen==Screen.HOME,{screen=Screen.HOME},icon={Text("⌂")},label={Text("الرئيسية")})
 NavigationBarItem(screen==Screen.WALLET,{screen=Screen.WALLET},icon={Text("◈")},label={Text("المحفظة")})
 NavigationBarItem(screen==Screen.ACCOUNT,{screen=Screen.ACCOUNT},icon={Text("●")},label={Text("حسابي")})
 if(admin) NavigationBarItem(screen==Screen.ADMIN,{screen=Screen.ADMIN},icon={Text("⚙")},label={Text("الإدارة")})
 }} ){ p-> when(screen){
 Screen.HOME->Home(points,claimed,p){if(!claimed){points+=10;claimed=true}}
 Screen.WALLET->Wallet(points,available,reserved,requests,p){n,last->if(n>=MIN_POINTS&&n<=available)requests.add(0,Withdrawal("FL-${System.currentTimeMillis()}",n,last))}
 Screen.ACCOUNT->Column(Modifier.fillMaxSize().padding(p).padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){Text("حسابي",color=Color.White,fontSize=26.sp);Row(verticalAlignment=Alignment.CenterVertically){Text("وضع المالك التجريبي",color=Color.White,modifier=Modifier.weight(1f));Switch(admin,{admin=it})};if(admin)Button(onClick={screen=Screen.ADMIN}){Text("فتح لوحة المالك")};Text("في النسخة الإنتاجية تُحدد صلاحية المالك من الخادم.",color=Muted)}
 Screen.ADMIN->Admin(requests,p,{it.status="تمت الموافقة"},{it.status="مكتمل";points=(points-it.points).coerceAtLeast(0)},{it.status="مرفوض"})
 }}}
}
@Composable private fun Home(points:Long,claimed:Boolean,p:PaddingValues,collect:()->Unit){
 LazyColumn(Modifier.fillMaxSize().padding(p).padding(20.dp),contentPadding=PaddingValues(vertical=22.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){
 item{Text("مرحباً بك في",color=Muted);Text("FLOTR",color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Black)}
 item{Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=Color.Transparent)){Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF6339C9),Color(0xFF25204D),Panel))).padding(22.dp)){Text("إجمالي نقاطك",color=Color.White);Text("%,d".format(points),color=Color.White,fontSize=38.sp,fontWeight=FontWeight.Bold);Text("نقطة • لا تنتهي صلاحيتها",color=Mint);Text("1,000 نقطة = 1,000 دينار عراقي",color=Color.White)}}}
 item{Card(colors=CardDefaults.cardColors(containerColor=Panel)){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){Text("المكافأة اليومية",color=Color.White);Text("اجمع 10 نقاط تجريبية",color=Muted);Button(onClick=collect,enabled=!claimed,modifier=Modifier.fillMaxWidth()){Text(if(claimed)"تم الاستلام" else "استلام 10 نقاط")}}}}
 item{Text("رصيدك ينتقل للشهر التالي تلقائياً ولا يتم تصفيره.",color=Mint)}
 }}
@Composable private fun Wallet(points:Long,available:Long,reserved:Long,requests:List<Withdrawal>,p:PaddingValues,submit:(Long,String)->Unit){
 var dialog by remember{mutableStateOf(false)};var amount by remember{mutableStateOf("1000")};var last4 by remember{mutableStateOf("")}
 val n=amount.toLongOrNull()?:0
 if(dialog)AlertDialog(onDismissRequest={dialog=false},title={Text("طلب سحب")},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
 Text("1,000 نقطة = 1,000 د.ع. الحد الأدنى 1,000.")
 OutlinedTextField(amount,{amount=it.filter(Char::isDigit)},label={Text("عدد النقاط")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number))
 OutlinedTextField(last4,{last4=it.filter(Char::isDigit).take(4)},label={Text("آخر 4 أرقام من البطاقة")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number))
 Text("المتاح: $available نقطة",color=Muted);Text("لا تدخل رقم البطاقة الكامل أو CVV أو OTP.",color=Color(0xFFFFCC80))
 }},confirmButton={TextButton(enabled=n>=MIN_POINTS&&n<=available&&last4.length==4,onClick={submit(n,last4);dialog=false}){Text("إرسال")}},dismissButton={TextButton(onClick={dialog=false}){Text("إلغاء")}})
 LazyColumn(Modifier.fillMaxSize().padding(p).padding(20.dp),contentPadding=PaddingValues(vertical=18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 item{Text("المحفظة",color=Color.White,fontSize=26.sp,fontWeight=FontWeight.Bold)}
 item{Card(colors=CardDefaults.cardColors(containerColor=Panel)){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){Text("إجمالي النقاط: $points",color=Color.White);Text("المتاح: $available",color=Mint);Text("المحجوز: $reserved",color=Color(0xFFFFD180));Text("القيمة المتاحة: $available د.ع",color=Muted);Button(onClick={dialog=true},enabled=available>=MIN_POINTS,modifier=Modifier.fillMaxWidth()){Text("طلب سحب Mastercard عراقية")}}}}
 item{Text("طلبات السحب",color=Color.White,fontSize=19.sp)}
 if(requests.isEmpty())item{Text("لا توجد طلبات حالياً",color=Muted)}
 items(requests){r->Card(colors=CardDefaults.cardColors(containerColor=Panel)){Column(Modifier.padding(14.dp)){Text("${r.points} نقطة • ${r.points} د.ع",color=Color.White);Text("${r.id} • **** ${r.last4}",color=Muted);Text(r.status,color=Mint)}}}
 item{Text("نسخة تجريبية محلية؛ الطلبات لا تصل إلى أجهزة أخرى قبل ربط الخادم.",color=Muted,fontSize=11.sp)}
 }}
@Composable private fun Admin(requests:List<Withdrawal>,p:PaddingValues,approve:(Withdrawal)->Unit,complete:(Withdrawal)->Unit,reject:(Withdrawal)->Unit){
 LazyColumn(Modifier.fillMaxSize().padding(p).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 item{Text("لوحة المالك",color=Color.White,fontSize=26.sp,fontWeight=FontWeight.Bold);Text("وافق، حوّل يدوياً، ثم أكد نجاح التحويل.",color=Muted)}
 if(requests.isEmpty())item{Text("لا توجد طلبات.",color=Muted)}
 items(requests){r->Card(colors=CardDefaults.cardColors(containerColor=Panel)){Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
 Text("${r.points} د.ع • ${r.id}",color=Color.White);Text("Mastercard **** ${r.last4}",color=Muted);Text(r.status,color=Mint)
 if(r.status=="قيد المراجعة"){Row{Button(onClick={approve(r)},modifier=Modifier.weight(1f)){Text("موافقة")};OutlinedButton(onClick={reject(r)},modifier=Modifier.weight(1f)){Text("رفض")}}}
 if(r.status=="تمت الموافقة"||r.status=="قيد التحويل"){Button(onClick={r.status="قيد التحويل"},modifier=Modifier.fillMaxWidth()){Text("قيد التحويل")};Button(onClick={complete(r)},modifier=Modifier.fillMaxWidth()){Text("تأكيد التحويل وخصم النقاط")}}
 if(r.status=="مرفوض")Text("رُفض الطلب؛ النقاط غير مخصومة.",color=Color(0xFFFFCC80))
 if(r.status=="مكتمل")Text("تم التأكيد في النموذج التجريبي.",color=Mint)
 }}}
 item{Text("وضع المالك هنا تجريبي وغير محمي. لا تستخدمه لإدارة أموال حقيقية قبل ربط صلاحيات خادمية.",color=Color(0xFFFFCC80),fontSize=12.sp)}
 }}
