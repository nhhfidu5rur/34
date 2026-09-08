package com.excelmanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ExcelApp()
                }
            }
        }
    }
}

@Composable
fun ExcelApp() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    if (!isLoggedIn) {
        // شاشة تسجيل الدخول
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("أهلاً بك في مدير الاكسل", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("أدخل كلمة المرور") },
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = {
                if (password == "000") {
                    isLoggedIn = true
                } else {
                    Toast.makeText(context, "كلمة المرور خاطئة!", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("دخول")
            }
        }
    } else {
        // الشاشة الرئيسية
        MainScreen()
    }
}

@Composable
fun MainScreen() {
    var categories by remember { mutableStateOf(listOf<String>()) }
    var newCategory by remember { mutableStateOf("") }
    var dataEntries by remember { mutableStateOf(mapOf<String, String>()) }
    var entryValue by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("إدارة الملفات", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Blue)
        Spacer(modifier = Modifier.height(20.dp))

        // إضافة قائمة جديدة
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = newCategory,
                onValueChange = { newCategory = it },
                label = { Text("اسم القائمة (مثلاً: سحبات)") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                if (newCategory.isNotBlank()) {
                    categories = categories + newCategory
                    newCategory = ""
                }
            }, modifier = Modifier.padding(start = 8.dp)) {
                Text("إضافة")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("إدخال البيانات اليومية", fontWeight = FontWeight.Bold)

        // اختيار قائمة لإدخال القيمة لها
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(categories) { cat ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(cat, modifier = Modifier.weight(1f))
                    TextField(
                        value = dataEntries[cat] ?: "",
                        onValueChange = { 
                            dataEntries = dataEntries + (cat to it) 
                        },
                        label = { Text("القيمة") },
                        modifier = Modifier.width(120.dp)
                    )
                }
            }
        }

        // زر التصدير
        Button(
            onClick = {
                val date = SimpleDateFormat("yyyy/M/d", Locale.getDefault()).format(Date())
                val fileName = "ExcelData.csv"
                try {
                    val file = File(context.getExternalFilesDir(null), fileName)
                    val writer = file.printWriter()
                    // كتابة التاريخ في أول السطر
                    writer.println("التاريخ: $date")
                    writer.println("القائمة,القيمة")
                    dataEntries.forEach { (cat, value) ->
                        writer.println("$cat,$value")
                    }
                    writer.close()
                    Toast.makeText(context, "تم التصدير بنجاح في: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "خطأ في التصدير: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
        ) {
            Text("تصدير ملف Excel (CSV)", color = Color.White)
        }
    }
}
