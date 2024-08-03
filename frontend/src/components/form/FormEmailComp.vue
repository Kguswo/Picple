<script setup>
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import { validateEmailPattern, setFormMessage } from "@/composables/validation";
import { ref } from "vue";
import { useRouter } from "vue-router";
import Swal from "sweetalert2";
import { sendAuthNumberApi, verifyAuthNumberApi } from "@/api/userApi";
import { useUserStore } from "@/stores/userStore";
import { storeToRefs } from "pinia";
import { useFormStore } from "@/stores/formStore";

const props = defineProps({
	path: String,
});

const router = useRouter();
const userStore = useUserStore();
const formStore = useFormStore();

const { verifiedEmail } = storeToRefs(userStore);
const { email, authNumber, emailField, authNumberField } = storeToRefs(formStore);
formStore.initForm([email, authNumber], [emailField, authNumberField]);

const isSend = ref(false);

const sendAuthNumber = async (e) => {
	e.stopPropagation();
	emailField.value.message = validateEmailPattern(email.value.value);
	if (formStore.focusInputField(emailField)) {
		return;
	}

	const data = await sendAuthNumberApi(email.value.value);
	if (!data) {
		return;
	}
	if (!data.isSuccess) {
		await Swal.fire({ icon: "error", title: `${data.message}`, width: 600 });
		return;
	}
	emailField.value.message = { text: `인증번호를 전송하였습니다.`, isError: false };
	isSend.value = true;
	// todo: 제한시간 표시
};

const verifyEmail = async () => {
	emailField.value.message = !isSend.value
		? setFormMessage(`이메일 인증이 필요합니다.`, true)
		: setFormMessage(``, false);
	authNumberField.value.message = !authNumber.value.value
		? setFormMessage(`인증번호를 입력해주세요.`, true)
		: setFormMessage(``, false);
	if (emailField.value.message.text) {
		emailField.value.focusInput();
		return;
	}
	if (authNumberField.value.message.text) {
		authNumberField.value.focusInput();
		return;
	}
	// todo: 제한시간 검사
	const data = await verifyAuthNumberApi(email.value.value, authNumber.value.value);
	if (!data) {
		return;
	}
	console.log(data);
	if (!data.isSuccess) {
		await Swal.fire({ icon: "error", title: `${data.message}`, width: 600 });
		return;
	}
	verifiedEmail.value = email.value.value;
	await Swal.fire({ icon: "success", title: "이메일 인증에 성공했습니다.", width: 600 });
	router.push({ path: props.path });
};
</script>

<template>
	<form class="form-content" @keyup.enter.prevent="verifyEmail">
		<FormInputComp :inputParams="email" :isSend="isSend" ref="emailField">
			<FormButtonComp
				size="small"
				@click="sendAuthNumber"
				@keyup.enter.prevent="sendAuthNumber"
				@keydown.enter.prevent
				>인증</FormButtonComp
			>
		</FormInputComp>

		<FormInputComp :inputParams="authNumber" ref="authNumberField" class="mt-10" />

		<FormButtonComp size="big" @click="verifyEmail">다음</FormButtonComp>
	</form>
</template>

<style scoped></style>
