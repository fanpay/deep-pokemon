<script setup>
import {ref} from "vue";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Battle from "@/components/Battle.vue";

const props = defineProps({
  playerName: {
    type: String,
    required: true
  },
  tourPlayer: {
    type: Boolean,
    required: false,
    default: false
  }
});

const apiUrl = import.meta.env.VITE_BACKEND_URL;
const battleList = ref()
const totalRecords = ref();
const page = ref(0);
const row = ref(25);

async function queryBattle(page, row) {
  let encodeName = encodeURIComponent(props.playerName);
  let url;
  if (props.tourPlayer) {
    url = `${apiUrl}/api/tour/player/${encodeName}/battle?page=${page}&row=${row}`;
  } else {
    url = `${apiUrl}/api/player/${encodeName}/battle?page=${page}&row=${row}`;
  }
  const res = await fetch((url), {
        method: "GET"
      }
  )
  const response = await res.json();
  battleList.value = response.data;
  totalRecords.value = response.totalRecords;
}

function onPage(event) {
  queryBattle(event.page, event.rows);
}

function rowStyle(row) {
  let backgroundColor;
  if (!props.tourPlayer) {
    backgroundColor = row.winner.toLowerCase() === props.playerName.toLowerCase() ? '#a3cfec' : '#e2b6b3';
  } else {
    backgroundColor = row.winSmogonPlayerName.toLowerCase() === props.playerName.toLowerCase() ? '#a3cfec' : '#e2b6b3';
  }
  return {backgroundColor: backgroundColor, margin: 0};
}

queryBattle(page.value, row.value);
</script>

<template>
  <DataTable :value="battleList" class="min-w-max" lazy paginator :rows="20" :rowsPerPageOptions="[5, 10, 20, 50]"
             :totalRecords="totalRecords" @page="onPage($event)" :scrollable="false"
             tableStyle="min-width: 50rem" :row-style="rowStyle">
    <Column field="battle" header="battle" :style="{ display:'flex', width:'100%', padding: 0 }"
            :headerStyle="{display:'none'}">
      <template #body="{data}">
        <Battle :data="data" :player-name="playerName" :tourPlayer="props.tourPlayer"/>
      </template>
    </Column>
  </DataTable>
</template>