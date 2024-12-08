<script setup>
import {ref} from "vue";
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Team from '@/components/Team.vue'
import ProgressSpinner from "primevue/progressspinner";


// 在需要使用后端 URL 的地方
const apiUrl = import.meta.env.VITE_BACKEND_URL;
const loading = ref(true);
const loadFail = ref(false);
const rank = ref(null)
const page = ref(0);
const row = ref(20);
const totalRecords = ref(null);
const emptyTeam = ref({
  pokemons: [{name: "null"}, {name: "null"}, {name: "null"}, {name: "null"}, {name: "null"}, {name: "null"}]
});

async function fetchData(page, row) {
  rank.value = null
  const res = await fetch(`${apiUrl}/api/rank?page=${page}&row=${row}`, {
        method: "GET"
      }
  )

  if (res.ok) {
    const response = await res.json();
    rank.value = response.data;
    totalRecords.value = response.totalRecords;
    loading.value = false;
  } else {
    loadFail.value = true;
    loading.value = false;
  }

}

async function onPage(event) {
  loading.value = true;
  await fetchData(event.page, event.rows);
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  });
}

fetchData(page.value, row.value)
</script>

<template>
  <DataTable :value="rank" v-show="loading===false && loadFail===false" class="ladder" lazy paginator :rows="20" :rowsPerPageOptions="[5, 10, 20, 50]"
             :totalRecords="totalRecords" @page="onPage($event)" :scrollable="false" stripedRows tableStyle="min-width: 50rem">
    <Column field="rank" header="排名"
            :style="{ width:'5%' }"></Column>
    <Column field="name" header="玩家名" :style="{ width:'10%' }">
      <template #body="{data}">
        <router-link :to="`/player-record?name=${data.name}`" class="text-black">
          {{ data.name }}
        </router-link>
      </template>
    </Column>
    <Column field="elo" header="elo" :style="{ width:'5%' }"></Column>
    <Column field="gxe" header="gxe" :style="{ width:'5%' }"></Column>
    <Column field="recentTeam" :style="{ width:'20%', 'text-align': 'center'}">
      <template #header>
        <div class="flex-1 text-center">最近使用队伍</div>
      </template>
      <template #body="{data}">
        <div class="flex justify-center">
          <div class="team-list" v-if="data.recentTeam.length !== 0">
            <Team v-for="team in data.recentTeam" :team="team" :compact="false"></Team>
          </div>
          <div v-else>
            <Team :team="emptyTeam" :compact="false"></Team>
          </div>
        </div>
      </template>
    </Column>
  </DataTable>
  <ProgressSpinner v-if="loading"/>
  <p v-if="loadFail" class="mt-[60px]">load ladder fail.</p>
</template>

<style scoped>
/*排行榜表格样式*/
.ladder {
  min-width: max-content;
  width: 90%;
  margin: 60px auto 0; /*表格下移以适应绝对定位的导航栏*/
}

.team-list {
  border: 0;
  margin: 0;
}
</style>