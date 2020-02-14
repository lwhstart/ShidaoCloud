package com.shidao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.shidao.model.WsMessage;
import com.shidao.enums.MessageType;
import com.shidao.service.CrmCustomerNoticeService;
import com.shidao.service.WsMessageService;
import com.shidao.util.JsonUtil;
import com.shidao.util.NotLoginException;
import com.shidao.websocket.MyWebSocket;

@RestController
@RequestMapping(value = "/wsMessage")
public class WsMessageController extends BaseController {

	@Autowired
	private WsMessageService wsMessageService;

	@Autowired
	private CrmCustomerNoticeService customerNoticeService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject readHistory(HttpServletRequest request, WsMessage condition, Integer pageNum, Integer pageSize) {
		try {
			if (condition.getSender() == null && request.getSession(true).getAttribute("uid") != null) {
				condition.setSender(request.getSession().getAttribute("uid").toString());
			}
			return JsonUtil.succeedJson(wsMessageService.list(condition, pageNum, pageSize));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/read/{id}", method = RequestMethod.POST)
	public JSONObject ReadId(HttpServletRequest request, @PathVariable(value = "id") Integer id) {
		try {
			return JsonUtil.succeedJson(wsMessageService.read(id));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/unread")
	public JSONObject Unread(HttpServletRequest request, @RequestParam String sender, String receiver) {
		try {
			if ((receiver == null || receiver.isEmpty()) && request.getSession(true).getAttribute("uid") != null) {
				receiver = request.getSession().getAttribute("uid").toString();
			}
			return JsonUtil.succeedJson(wsMessageService.Unread(sender, receiver));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	/**
	 * @author 创建人:liupengyuan,时间:2018年8月1日 功能:未读消息20条
	 * @param request
	 * @param sender
	 * @param receiver
	 * @return
	 */
	@GetMapping(value = "/read")
	public JSONObject read(HttpServletRequest request, @RequestParam String sender, String receiver) {
		try {
			if (receiver == null || receiver.isEmpty()) {
				String uid = String.valueOf(request.getSession().getAttribute("uid"));
				if (uid == null || uid.isEmpty()) {
					throw new NotLoginException();
				}
				receiver = uid;
			}
			return JsonUtil.succeedJson(wsMessageService.read(sender, receiver));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/{receiver}/unreadNoticeCount")
	public JSONObject Unread(HttpServletRequest request, @PathVariable(value = "receiver") String receiver) {
		try {
			return JsonUtil
					.succeedJson(wsMessageService.getUnreadCount(WsMessage.SYSTEM_ID, receiver, MessageType.notice));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/{receiver}/systemMessages", method = RequestMethod.GET)
	public JSONObject readHistory(HttpServletRequest request, @PathVariable(value = "receiver") String receiver,
			Integer pageNum, Integer pageSize) {
		try {
			WsMessage condition = new WsMessage();
			condition.setSender(WsMessage.SYSTEM_ID);
			condition.setReceiver(receiver);
			condition.setType(MessageType.notice);
			return JsonUtil.succeedJson(wsMessageService.list(condition, pageNum, pageSize));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/last/{sender}/{receiver}", method = RequestMethod.GET)
	public JSONObject last(HttpServletRequest request, @PathVariable(value = "sender") String sender,
			@PathVariable(value = "receiver") String receiver) {
		try {
			if ((receiver == null || receiver.isEmpty()) && request.getSession(true).getAttribute("uid") != null) {
				receiver = request.getSession().getAttribute("uid").toString();
			}
			WsMessage condition = new WsMessage();
			condition.setSender(sender);
			condition.setReceiver(receiver);
			condition.setDescending(true);
			return JsonUtil.succeedJson(wsMessageService.list(condition, 1, 1).get(0));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public JSONObject insert(WsMessage message) {
		try {
			wsMessageService.insertSelective(message);
			return JsonUtil.succeedJson(message.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(WsMessage message) {
		try {
			wsMessageService.updateByPrimaryKeySelective(message);
			return JsonUtil.succeedJson(message.getId());
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/{customerCid}/unreadNotice", method = RequestMethod.GET)
	public JSONObject listCustomerInfo(@PathVariable(name = "customerCid") String customerCid) {
		try {
			return JsonUtil.succeedJson(customerNoticeService.getCustomerUnreadNotice(customerCid));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "readCustomerNotice", method = RequestMethod.POST)
	public JSONObject readCustomerNotice(Integer customerId, Integer noticeId) {
		try {
			wsMessageService.readCustomerNotice(customerId, noticeId);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/customer/{customerCid}/unreadCount")
	public JSONObject customerUnreadCount(HttpServletRequest request,
			@PathVariable(value = "customerCid") String customerCid) {
		try {
			return JsonUtil.succeedJson(wsMessageService.getCustomerUnreadCount(customerCid));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@RequestMapping(value = "/socketSession/onLine/{customerCid}")
	public JSONObject UserSocketSession(@PathVariable(value = "customerCid") String customerCid) {
		try {
			return JsonUtil.succeedJson(MyWebSocket.userSocketSessionMap.containsKey(customerCid));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@GetMapping(value = "/load/{sender}/{receiver}")
	public JSONObject weixinSmallProgramLoad(Integer maxId, Integer pageSize,
			@PathVariable(value = "sender") String sender, @PathVariable(value = "receiver") String receiver) {
		try {
			if (pageSize == null) {
				pageSize = 8;
			}
			return JsonUtil.succeedJson(wsMessageService.weixinSmallProgramLoad(maxId, sender, receiver, pageSize));
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}

	@PostMapping(value = "/updateRead/{sender}/{receiver}")
	public JSONObject updateRead(@PathVariable(value = "sender") String sender,
			@PathVariable(value = "receiver") String receiver) {
		try {
			wsMessageService.updateRead(sender, receiver);
			return JsonUtil.succeedJson();
		} catch (Exception e) {
			return JsonUtil.errjson(e);
		}
	}
}
