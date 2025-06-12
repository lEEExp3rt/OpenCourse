<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { useInteractionStore } from '@/stores/interaction'
import { ElTabs, ElTabPane, ElButton, ElInput, ElTextarea } from 'element-plus'

const route = useRoute()
const activeTab = ref('forum') // 默认选中资源列表标签
const courseId = Number(route.params.id)
const InteractionStore = useInteractionStore()

const handleTabClick = (tab: any) => {
  if (tab.paneName === 'resource') {
    switch_to_Resources()
  }
}

const fetchCourseInteration = async () => {
  console.log('Fetching course interaction data for course ID:', courseId)
  try {
    await InteractionStore.fetchCourseInteration(courseId)
  } catch (error) {
    console.error('获取课程讨论区数据失败:', error)
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

onMounted(() => {
  fetchCourseInteration()
})

function getRandomAvatar(name) {
  const avatars = [
    "https://www.cc98.org/static/images/default_avatar_girl.png",
    "https://www.cc98.org/static/images/default_avatar_boy.png",
    "/static/images/心灵头像.gif",
  ];
  return avatars[name.length % avatars.length];
}

function like(comment) {

}

function formatDate(dateStr) {
  const date = new Date(dateStr);
  return date.toLocaleString();
}
</script>


<template>
  <div>
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="资源列表" name="resource"></el-tab-pane>
        <el-tab-pane label="讨论区" name="forum"></el-tab-pane>
      </el-tabs>

    <div class="center" style="width: 70rem; margin-right: 1px;">
      <div
        class="reply"
        v-for="post in InteractionStore.commentList"
        :key="post.id"
      >
        <!-- 顶部区域：userMessage -->
        <div class="userMessage">
          <!-- 左侧：用户名、匿名描述 -->
          <div class="userMessage-left">
            <div class="username">{{ post.userName }}</div>
          </div>
        </div>

        <div class="column" style="justify-content: space-between; width: 55.5rem; position: relative;">

          <!-- 正文内容 -->
          <div class="reply-content">
            <div style="align-self: center; margin-top: 1rem; margin-bottom: -1rem;"></div>
            <div class="substance">
              <article class="content-article">{{ post.content }}</article>
            </div>
          </div>

          <div class="column" style="margin-top: 1rem; width: 52rem; margin-bottom: 0.5rem;">
            <div class="comment1">
              <!-- 时间信息 -->
              <div style="width: 40rem; margin-left: 1.2rem; font-size: 0.8rem;">
                <span>发表于 {{ publishTime }}</span>
                <span style="margin-left: 1rem;" v-if="editedTime">该帖最后由 匿名 在 {{ editedTime }} 编辑</span>
              </div>

              <!-- 点赞/点踩/操作 -->
              <div class="row" style="align-items: center;">
                <div :id="'like' + postId" class="upup" style="margin-right: 0.7rem;" @click="handleLike">
                  <i title="赞" class="fa fa-thumbs-o-up fa-lg"></i>
                  <span class="commentProp"> {{ likes }}</span>
                </div>
                <div :id="'dislike' + postId" class="downdown" @click="handleDislike">
                  <i title="踩" class="fa fa-thumbs-o-down fa-lg"></i>
                  <span class="commentProp"> {{ dislikes }}</span>
                </div>
                <div id="commentlike">
                  <div class="operation1" @click="handleRate">评分</div>
                  <div class="operation1" @click="handleQuote">引用</div>
                  <div class="operation1">
                    <a :href="`/topic/${topicId}/postid/${postId}`">追踪</a>
                  </div>
                </div>
              </div>
              <div class="row" style="width: 100%; justify-content: center;">  </div>
            </div>
          </div>

          <!-- 中间行留白 -->
          <div class="row" style="width: 100%; justify-content: center;"></div>

            
        </div>

        <!-- 删除 -->
        <div class="reply-floor" :style="{ visibility: post.isOwner ? 'visible' : 'hidden' }" @click="handleDelete(post.id)">
          <el-icon color="#fff" size="18"><Delete /></el-icon>
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

.center {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 71.2rem;
}

.reply {
  width: 100%;
  margin-top: 1rem;
  border: #3578bc solid 2px;
  background-color: #fff;
  flex-direction: row;
  display: flex;
  position: relative;
}

.userMessage {
  align-items: flex-start;
  background-color: #3578bc;
  color: #fff;
  width: 20.5rem;
  display: flex;
  padding-top: 1rem;
  padding-bottom: 1rem;
  font-size: .75rem;
}


.userMessage-left {
  padding-top: 1.5rem;
  width: 8.3rem;
}

.username {
  color: white;
  font-size: 1rem;
  font-weight: bold;
  margin-left: 1rem;
  margin-top: -0.8rem;
} 

.reply-content {
	width: 100%;
	display: flex;
	align-self: flex-start;
	flex-direction: column;
	box-sizing: border-box;
}

.substance {
  display: flex;
  margin-top: 2rem;
  margin-left: 1.5rem;
  width: 52rem;
  word-wrap: break-word;
  color: #000;
  font-family: 微软雅黑;
}
.awardInfo {
  margin-top: 1rem;
  padding-top: 0.75rem;
  border-top: 1px solid #4b5563;
}



.column {
  display: flex;
  flex-direction: column;
}

.comment1 {
  display: flex;
  flex-direction: row;
  font-size: .75rem;
  align-items: flex-end;
  padding-bottom: .3rem;
  color: #3578bc;
  align-items: center;
  justify-content: space-between;
}

.upup {
  cursor: pointer;
  display: flex;
}


.row {
  display: flex;
  align-items: center;
}


.reply .reply-floor,
.reply .reply-floor-small {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  right: -1.2rem;
  background-color: #f56c6c; /* 使用 danger 红色 */
  text-align: center;
  line-height: 40px;
  position: relative;
  top: 70px;
  color: #fff;
  font-family: "微软雅黑";
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.reply .reply-floor:hover {
  background-color: #532927; /* hover 效果 */
}
</style>

