// stores/interaction.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import InteractionApi from '@/api/interaction'

export const useInteractionStore = defineStore('interaction', () => {
  const commentList = ref([])

  // 获取某课程下的所有评论
  const fetchCourseInteration = async (course_id) => {
    try {
      const res = await InteractionApi.get_all_interactions(course_id)
      if (res.success === true && Array.isArray(res.data)) {
        commentList.value = res.data
      } else {
        console.warn(`获取评论失败：res =`, res)
        commentList.value = getDefaultComments()
      }
    } catch (err) {
      console.error('获取评论出错:', err)
      commentList.value = getDefaultComments()
    }
  }

  // 发布评论
  const postComment = async (content) => {
    try {
      const res = await InteractionApi.post_comment(content)
      if (res.success === true && res.data) {
        commentList.value.push(res.data)
        console.log("评论发布成功：", res.data)
      } else {
        console.warn('评论发布失败：', res)
      }
    } catch (err) {
      console.error('发布评论出错：', err)
    }
  }

  // 删除评论
  const deleteComment = async (comment_id) => {
    try {
      const res = await InteractionApi.delete_comment(comment_id)
      if (res.success === true) {
        commentList.value = commentList.value.filter(c => c.id !== comment_id)
        console.log(`评论 ${comment_id} 删除成功`)
      } else {
        console.warn(`删除评论失败：res =`, res)
      }
    } catch (err) {
      console.error(`删除评论 ${comment_id} 时出错：`, err)
    }
  }

  // 点赞评论
  const likeComment = async (comment_id) => {
    try {
      const res = await InteractionApi.like_comment(comment_id)
      if (res.success === true) {
        const comment = commentList.value.find(c => c.id === comment_id)
        if (comment) comment.likes++
      } else {
        console.warn(`点赞评论失败：res =`, res)
      }
    } catch (err) {
      console.error(`点赞评论 ${comment_id} 时出错：`, err)
    }
  }

  // 取消点赞评论
  const unlikeComment = async (comment_id) => {
    try {
      const res = await InteractionApi.unlike_comment(comment_id)
      if (res.success === true) {
        const comment = commentList.value.find(c => c.id === comment_id)
        if (comment && comment.likes > 0) comment.likes--
      } else {
        console.warn(`取消点赞评论失败：res =`, res)
      }
    } catch (err) {
      console.error(`取消点赞评论 ${comment_id} 时出错：`, err)
    }
  }

  function getDefaultComments() {
    return [
      {
        id: 0,
        content: `生活在对方的不满里。这些不满存在于诸多生活中的小细节。
                例如我花大功夫做了午饭，对方会抱怨我饭后不洗碗。
                例如我开车送她上班走了一条她不喜欢的路（她嫌路况复杂），对方也会抱怨。
                有时甚至不需要发生具体的事情，她会突然开始预警我做坏事，然后开始指责我。例如上面说的做饭，周末早上起床，我说想要去超市买菜做饭，对方突然来一句：
                “别买了，你做完饭又不洗碗。”
                兴致勃勃的我突然被浇了一大盆冷水。
                但是家里明明有洗碗机，非不舍得用。
                似乎我在这段感情里从未感受到对方给的情绪价值，回忆起来几年的婚姻她从未主动叫我一声“老公”，她跟朋友称呼我都是叫一个简单的姓，甚至不愿意在姓前面加一个“小”字。
                更别提鼓励与肯定了。
                所有的事情都尝试跟对方沟通过，但是都无果。我说不过对方。
                时间久了，我只要跟对方相处，就会一直处于时刻在提防对方情绪的状态，害怕对方随时会将工作/社交上的情绪传染过来，害怕对方随时会对我进行无端的抱怨和指责。
                我提出过想要去做亲密关系情感类的心理咨询，被对方严厉拒绝了，理由是“我没病，不需要去做心理咨询。”
                我说，“这个不是生病了才去做的。”
                她说，“这个就是病，抑郁症都是残疾人。”
                我本身是抑郁康复，很担心自己又陷入什么都不想做的状态，因此一般自己有什么想法都会拼命鼓励自己去做。但是这些想法如果要分享给对方的话，大概率得到的是否定与反驳。
                例如某天特别想吃小炒黄牛肉，分享给对方后对方突然来了一句：
                “别吃了，自己在家做吧，省点钱。”
                但是我那天在上班。一瞬间就什么都不想吃了，后来饿肚子饿了一整天。
                再比如有天想喝水果汽水，告诉对方我要出门买水果汽水后，她说：
                “别喝汽水了，家里有牛奶，你喝牛奶吧。”
                我说：“我不想喝牛奶。”
                她说：“那你就喝热水。”
                于是我什么都不想喝了，但是嘴巴又干又苦，最后甚至一宿没睡着。
                最近也许是因为压力过大，似乎这些抱怨和指责在我眼里变得更尖锐了。有时会因为很小很小的事闹不愉快。
                例如，前一天晚上睡觉前洗头发+吹干，导致第二天睡醒自己头发乱乱的，她也要抱怨一句，“你干嘛不早上睡醒了再洗头发？”
                例如，刷完碗后有一个杯子忘记刷了，她也会跑过来抱怨我一句，“本来我看你主动洗碗还蛮开心的，没想到这个杯子你还是没给我洗。”
                再例如，睡觉前玩了五分钟手机，对方也会来指责我，“你天天睡觉前玩手机当然睡不着。”
                我本身是很敏感的人，这些细节都能很直接的觉察到，我也尝试过跟对方说，“你不要一直是指责我和抱怨我，家庭里不应该是这样的沟通方式。”
                她说，“你凭什么教我说话？凭什么堵我的嘴？非要让我跪着求你的态度吗？”
                我说不过她，于是后来所有的这些细节发生后我都会保持沉默与木讷。所以我话越来越少，很多时候都是因为对方的指责在自己内耗。
                我感到深刻的绝望，因为这件事在我眼里已经是无法沟通、无法解决的了。`,
        rating: 10,
        userName: "周树人",
        likes: 5,
        createdAt: "2024-01-01T00:00:00Z",
        isLiked: true,
        isOwner: true
      },
      {
        id: 1,
        content: "内容不错，建议加点实战案例。",
        rating: 8,
        userName: "李白",
        likes: 3,
        createdAt: "2024-02-14T15:30:00Z",
        isLiked: false,
        isOwner: false
      },
      {
        id: 2,
        content: "讲解有点快，适合有基础的同学。",
        rating: 7,
        userName: "杜甫",
        likes: 1,
        createdAt: "2024-03-10T09:45:00Z",
        isLiked: false,
        isOwner: false
      },
      {
        id: 3,
        content: "真的太棒了！看完秒懂！",
        rating: 9,
        userName: "王小明",
        likes: 12,
        createdAt: "2024-04-01T18:20:00Z",
        isLiked: true,
        isOwner: false
      },
      {
        id: 4,
        content: "资源有点旧了，希望能更新一下。",
        rating: 6,
        userName: "林黛玉",
        likes: 0,
        createdAt: "2024-05-05T22:00:00Z",
        isLiked: false,
        isOwner: false
      },
      {
        id: 5,
        content: "支持一下，是干货。",
        rating: 8,
        userName: "鲁迅",
        likes: 4,
        createdAt: "2024-06-01T11:00:00Z",
        isLiked: true,
        isOwner: true
      }
    ]
  }
  

  return {
    commentList,
    fetchCourseInteration,
    postComment,
    deleteComment,
    likeComment,
    unlikeComment
  }
})
