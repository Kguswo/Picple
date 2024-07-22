import { ref } from "vue";

const useFormInput = (fields) => {
	const objects = {};
	if (fields) {
		for (let field of fields) {
			objects[field] = ref({ value: "", isFocused: false });
		}
	}

	const handleFocus = (field) => {
		field.isFocused = true;
	};

	const handleBlur = (field) => {
		field.isFocused = false;
	};

	return {
		objects,
		handleFocus,
		handleBlur,
	};
};

const enableFocus = () => {
	const inputContainers = document.getElementsByClassName("input-container");
	for (let divParent of inputContainers) {
		let inputChildren = divParent.getElementsByClassName("form-input");
		divParent.addEventListener("click", function () {
			inputChildren[0].focus();
		});
	}
};

export { useFormInput, enableFocus };
