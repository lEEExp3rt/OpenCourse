<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { useInteractionStore } from '@/stores/interaction'
import { ElTabs, ElTabPane, ElButton, ElInput, ElTextarea } from 'element-plus'
import { ThumbsUp, ThumbsUpFilled } from 'lucide-vue-next'
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
  const userComment = InteractionStore.commentList.find(comment => comment.isOwner === true)

  if (userComment) {
    newPost.value.content = userComment.content
    newPost.value.rating = userComment.rating || 0
  }
}

const newPost = ref({
  rating: 0,
  content: '',
})

const submitPost = async() => {
  if (!newPost.value.content) {
    alert('请输入您的看法')
    return
  }
  await InteractionStore.postComment({
    courseId: courseId,
    content:  newPost.value.content,
    rating: newPost.value.rating == 0 ? null : newPost.value.rating,
  })

  // 清空输入框
  fetchCourseInteration()

}


const switch_to_Resources = () => {
  // 切换到讨论区 
  const currentUrl = window.location.href
  const newUrl = currentUrl.replace(/\/forum$/, '/resources')
  window.location.href = newUrl
}

onMounted(() => {
  fetchCourseInteration(courseId)
})


const formatDate = (dateStr) =>{
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份从0开始
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    const second = String(date.getSeconds()).padStart(2, '0');
    return `${year}年${month}月${day}日 ${hour}:${minute}:${second}`;
}

const handleLike = async(post)=> {
  if (post.isLiked) {
    // 如果已经点赞，取消点赞
    await InteractionStore.unlikeComment(post.id)
  } else {
    // 如果未点赞，执行点赞操作
    await InteractionStore.likeComment(post.id)
  }
  await fetchCourseInteration() // 刷新评论列表
}

const handleDelete = async(post) =>
{
  await InteractionStore.deleteComment(post.id)
  await fetchCourseInteration() // 刷新评论列表
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
              <el-rate
              v-if="post.rating !== null"
              v-model="post.rating"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value} points"
            />
              <article class="content-article">{{ post.content }}</article>
            </div>
          </div>

          <div class="column" style="margin-top: 1rem; width: 52rem; margin-bottom: 0.5rem;">
            <div class="comment1">
              <!-- 时间信息 -->
              <div style="width: 40rem; margin-left: 1.2rem; font-size: 0.8rem;">
                <span>{{post.userName }}发表于 {{ formatDate(post.createdAt)}}</span>
              </div>

              <!-- 点赞/点踩/操作 -->
              <div class="row" style="align-items: center;">
                <div  class="upup" style="margin-right: 0.7rem;" @click="handleLike(post)">
                    <ThumbsUp 
                      :size="15"
                      :color="post.isLiked ? '#e74c3c' : '#000000'"
                      style="margin-right: 4px;"
                    />
                    <span class="commentProp"> {{ post.likes }} </span>
                </div>
              </div>
              <div class="row" style="width: 100%; justify-content: center;">  </div>
            </div>
          </div>

          <!-- 中间行留白 -->
          <div class="row" style="width: 100%; justify-content: center;"></div>

            
        </div>

        <!-- 删除 -->
        <div class="reply-floor" :style="{ visibility: post.isOwner ? 'visible' : 'hidden' }" @click="handleDelete(post)">
          <el-icon color="#fff" size="18"><Delete /></el-icon>
        </div>
      </div>
      <div class="new-post">
        <h4 class="text-lg font-semibold mb-2">发布看法</h4>
           <!-- 星星评分 -->
        <span class="text-sm text-gray-600">评分：</span>
        <el-rate v-model="newPost.rating"  />
        <el-input
          v-model="newPost.content"
          style="width: 100%; "
          type="textarea"
          placeholder="请输入您的看法"
          :rows="15"
        />
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
  width: 100%;
  max-width: 1200px;
  padding: 0 1rem;
  margin: 0 auto;
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
  overflow-x: auto;     /* 横向滚动条 */
	display: flex;
	align-self: flex-start;
	flex-direction: column;
	box-sizing: border-box;
}

.substance {
  display: flex;
  margin-top: 1rem;
  margin-left: 1.5rem;
  width: 52rem;
  word-wrap: break-word;
  color: #000;
  font-family: 微软雅黑;
  flex-direction: column;
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
.commentProp {
  margin-left: .2rem;
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

.new-post {
  width: 100%;
  margin-top: 1rem;
  padding: 1rem;
  background-color: #f9f9f9;
  border-radius: 8px;
  position: relative;
  margin-left: 20px;
}
</style>

