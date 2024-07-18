<script setup>
defineProps({
    title: String,
});
</script>

<template>
    <div class="account-content">
        <div id="title">{{ title }}</div>
        <slot></slot>
    </div>
</template>

<style>
@import "@/assets/css/account.css";
</style>
