const cluster = require('cluster');
const http = require('http');
const numCPUs = require('os').cpus().length;

const C10_OUTPUT = "0123456789";
const C100_OUTPUT = C10_OUTPUT + C10_OUTPUT + C10_OUTPUT + C10_OUTPUT + C10_OUTPUT + C10_OUTPUT + C10_OUTPUT + C10_OUTPUT + C10_OUTPUT + C10_OUTPUT;
const C1000_OUTPUT = C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT + C100_OUTPUT;
const C10000_OUTPUT = C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT + C1000_OUTPUT;
const C100000_OUTPUT = C10000_OUTPUT + C10000_OUTPUT + C10000_OUTPUT + C10000_OUTPUT + C10000_OUTPUT + C10000_OUTPUT + C10000_OUTPUT + C10000_OUTPUT + C10000_OUTPUT + C10000_OUTPUT;

const forkSlaves = function() {
	if (cluster.isMaster) {
		console.log('Master ' + process.pid + ' is running');

		// Fork workers.
		for (var i = 0; i < numCPUs; i++) {
			cluster.fork();
		}

		cluster.on('exit', function(worker) {
			console.log('worker ' + worker.process.pid + ' died');
		});
	} else {
		// Workers can share any TCP connection
		// In this case it is an HTTP server
		createServer();

		console.log('Worker ' + process.pid +' started');
	}
};

const buildPad = function(padding) {
	var pad = "";
	var size = 0;

	var sizes = [100000, 10000, 1000, 100, 10, 1];
    var consts = [C100000_OUTPUT, C10000_OUTPUT, C1000_OUTPUT, C100_OUTPUT, C10_OUTPUT, "1"];
    var currSizeIdx = 0;
    while (currSizeIdx <= sizes.length) {
    	while (padding - size >= sizes[currSizeIdx]) {
            pad += consts[currSizeIdx];
            size += sizes[currSizeIdx];
        }
        currSizeIdx ++;
    }
    return pad;
};

const createServer = function() {
	const express = require('express');
	const app = express();

	app.get('/sum', function(req, res) {
		// Not yet implemented, setting default.
		const delay = parseInt(req.query.delay, 10) | NaN;
		// const delay = 0;
		// const delay = NaN;

        const padding = parseInt(req.query.padding, 10) | NaN;

		// We don't really care about the value being returned, so saving CPU time.
		const result = req.query.nums.length;

        // const nums = (req.query.nums || "0");
		// var sum = 0;
		// for (var i=0; i<nums.length; i++) {
		//   sum += parseInt(nums[i], 10);
		// }
		// console.log("Replying " + sum);

		const pad = buildPad(padding);

		const respond = function() {
			return res.send("" + result + (pad.length > 0 ? "\n" + pad : ""));
		};

		if (isNaN(delay)) {
			respond();
		} else {
			setTimeout(respond, delay);
		}
	});

	const port = process.env.PORT || 3000;

  const server = http.createServer(app);
  server.setTimeout(10*60*1000); // 10 minutes

	server.listen(port, function() {
		console.log('Number generator app listening on port ' + port + '.');
	});
};

forkSlaves();
