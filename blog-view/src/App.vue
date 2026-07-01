<template>
	<div id="app">
		<router-view/>
		<ChatBot/>
	</div>
</template>

<script>
	import ChatBot from '@/components/ChatBot'

	export default {
		name: 'app',
		components: {ChatBot}
	}
</script>

<style>

</style>
