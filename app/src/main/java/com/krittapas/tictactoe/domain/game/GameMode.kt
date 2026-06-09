package com.krittapas.tictactoe.domain.game

enum class GameMode {
    STANDARD,  // XO ปกติ: ลงเต็มกระดานได้ มีเสมอได้
    INFINITE,  // หมากวนรอบ: เกินเพดานแล้วตัวเก่าสุดหาย ไม่มีเสมอ
}