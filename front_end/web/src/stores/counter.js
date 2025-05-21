import { defineStore } from "pinia";

export const useCounterStore = defineStore('counter',{
    state(){
        return {
            num:1
        }
    },
    actions:{
        inc(){
            this.num++
        }
    }

})