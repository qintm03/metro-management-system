<template>
    <div class="box">
        <div class="navi_group">
            <div v-for="(item, index) in buttonArr" 
            :key="index" 
            class="nav-item" 
            :class="[navitem(item.name)]"
            @click="navLink(item.name)"
            >               
            <img :src="getImageUrl(item.url)" alt="">

            </div>
        </div>

    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import "../../assets/css/navigation.css";

const router = useRouter();

const buttonArr = ref<any[]>([
    {
        name: 'HomePage',
        url: '安全基础.png',
        urlKey: 'HomePage'
    },
    {
        name: 'OpertionService',
        url: '重大危险源.png',
        urlKey: 'OpertionService'
    },
    {
        name: 'TripService',
        url: '双重预防.png',
        urlKey: 'TripService'
    },
    {
        name: 'TicketManagement',
        url: '特殊作业.png',
        urlKey: 'TicketManagement'
    }
]);
const getImageUrl = (url: string) => {
    return new URL(`../../assets/img/navigation/${url}`, import.meta.url).href;
};
const navitem  = (key: string) => {
    return `nav-item-${key}`;
}
const navLink = (key: string) => {
    router.push({ 
        path: `/${key}`
     });
}

</script>

<style scoped>

</style>