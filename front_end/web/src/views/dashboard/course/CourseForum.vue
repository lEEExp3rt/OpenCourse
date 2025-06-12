<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { useCourseStore } from '@/stores/course'
import { genFileId, type UploadInstance, type UploadProps, type UploadRawFile } from 'element-plus'

const route = useRoute()
const activeTab = ref('forum') // 默认选中资源列表标签
const courseId = Number(route.params.id)

const handleTabClick = (tab: any) => {
  if (tab.paneName === 'resource') {
    switch_to_Resources()
  }
}

// 模拟帖子的列表
const posts = ref([
  { id: 1, title: '欢迎来到讨论区！', content: '可以在这里畅所欲言～', author: '管理员' },
  { id: 2, title: '课程第二章的问题', content: '有没有人弄懂2-3节的推导？', author: '小张' }
])

const newPost = ref({
  title: '',
  content: '',
  author: ''
})

const postIdCounter = ref(3)

const submitPost = () => {
  if (!newPost.value.title || !newPost.value.content || !newPost.value.author) {
    alert('请填写完整的帖子信息')
    return
  }

  posts.value.unshift({
    id: postIdCounter.value++,
    title: newPost.value.title,
    content: newPost.value.content,
    author: newPost.value.author
  })

  // 清空输入框
  newPost.value.title = ''
  newPost.value.content = ''
  newPost.value.author = ''
}


const switch_to_Resources = () => {
  // 切换到讨论区 
  const currentUrl = window.location.href
  const newUrl = currentUrl.replace(/\/forum$/, '/resources')
  window.location.href = newUrl
}
</script>


<template>
    <div>
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="资源列表" name="resource"></el-tab-pane>
        <el-tab-pane label="讨论区" name="forum"></el-tab-pane>
      </el-tabs>
      <div class="forum-container">

        <div class="post-list space-y-4">
          <div v-for="post in posts" :key="post.id" class="post-item p-4 border rounded shadow-sm bg-white">
            <h4 class="text-lg font-bold">{{ post.title }}</h4>
            <p class="text-sm text-gray-500 mb-1">作者：{{ post.author }}</p>
            <p>{{ post.content }}</p>
          </div>
        </div>
        
        <div class="new-post mb-6 p-4 border rounded shadow-sm bg-white">
          <h3 class="text-lg font-semibold mb-2">发表新帖</h3>
          <input v-model="newPost.author" class="input" placeholder="昵称" />
          <input v-model="newPost.title" class="input mt-2" placeholder="标题" />
          <textarea v-model="newPost.content" class="textarea mt-2" placeholder="内容" rows="4" />
          <button class="submit-btn mt-2" @click="submitPost">发布</button>
        </div>

      </div>
  </div>
</template>

<style scoped>
.forum-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.input, .textarea {
  width: 100%;
  border: 1px solid #ccc;
  border-radius: 6px;
  padding: 8px;
  font-size: 14px;
}
.submit-btn {
  background-color: #409eff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
}
.submit-btn:hover {
  background-color: #66b1ff;
}
</style>
