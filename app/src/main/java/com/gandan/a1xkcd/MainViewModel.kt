package com.gandan.a1xkcd

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    /**
     * "month": "1",
    "num": 2,
    "link": "",
    "year": "2006",
    "news": "",
    "safe_title": "Petit Trees (sketch)",
    "transcript": "[[Two trees are growing on opposite sides of a sphere.]]\n{{Alt-title: 'Petit' being a reference to Le Petit Prince, which I only thought about halfway through the sketch}}",
    "alt": "'Petit' being a reference to Le Petit Prince, which I only thought about halfway through the sketch",
    "img": "https://imgs.xkcd.com/comics/tree_cropped_(1).jpg",
    "title": "Petit Trees (sketch)",
    "day": "1"
     */

    val num = MutableLiveData<Int>()
    val link = MutableLiveData<String>()
    val year = MutableLiveData<String>()
    val news = MutableLiveData<String>()
    val safeTitle = MutableLiveData<String>()
    val transcript = MutableLiveData<String>()
    val alt = MutableLiveData<String>()
    val img = MutableLiveData<Uri>()
    val title = MutableLiveData<String>()
    val day = MutableLiveData<String>()
}