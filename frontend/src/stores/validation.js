const patternEmail = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-za-z0-9\-]+/;

function validateEmail(email) {
	if (patternEmail.test(email)) {
		return true;
	} else {
		return false;
	}
}
