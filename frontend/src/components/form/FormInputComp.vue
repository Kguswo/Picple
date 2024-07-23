<script setup>
import { ref } from "vue";
import FormIconCancelComp from "@/components/form/FormIconCancelComp.vue";
import FormIconVisibilityComp from "@/components/form/FormIconVisibilityComp.vue";

const props = defineProps({
  params: Object,
});

const inputField = ref(null);
const maxLength = props.params.label === '이메일' ? 254 : props.params.label === '닉네임' ? 8 : props.params.label.includes('비밀번호') ? 16 : 10;

const focusInput = () => {
  inputField.value.focus();
};

const toggleVisibility = (newType) => {
  props.params.type = newType;
}

const cancelInput = () => {
  props.params.value = "";
}

defineExpose({
  focusInput,
})
</script>

<template>
  <div class="input-container" @click="focusInput">
    <input :type="params.type" v-model="params.value" ref="inputField" class="form-input"
      :class="{ 'has-content': params.value }" :maxlength="maxLength" />
    <label class="form-label">{{ params.label }}</label>

    <div class="form-input-etc">
      <FormIconCancelComp v-if="params.value" class="form-icon-cancel" @click="cancelInput" />
      <FormIconVisibilityComp v-if="params.label.includes('비밀번호') && params.value" class="form-icon-visibility"
        :type="params.type" @toggle-visibility="toggleVisibility" />
      <slot></slot>
    </div>
  </div>
</template>

<style scoped>

</style>
