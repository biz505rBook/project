package com.biz.product.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biz.product.domain.ProFileDTO;
import com.biz.product.domain.ProductDTO;
import com.biz.product.persistence.FileDao;
import com.biz.product.persistence.ProductDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductSerivce {

	@Autowired
	SqlSession sqlSession;

	@Autowired
	FileService fService;

	ProductDao proDao;

	FileDao fileDao;

	@Autowired
	public void NewDao() {
		this.proDao = sqlSession.getMapper(ProductDao.class);
		this.fileDao = sqlSession.getMapper(FileDao.class);
	}

	public ProductDTO findByPCode(String p_code) {
		return proDao.findByPCode(p_code);
	}

	public List<ProductDTO> findByPNames(String p_name) {
		return proDao.findByPNames(p_name);
	}

	public List<ProductDTO> selectAll() {
		return proDao.selectAll();
	}

	public int insert(ProductDTO proDTO, List<ProFileDTO> upFileList) {
		// TODO Auto-generated method stub

		// 코드 생성 부분
		String p_code = proDao.getMaxPCode();
		String p_prefixCode = "P";

		// 만약 상품테이블에 데이터가 하나 없을 경우
		// intPCode 를 1로 세팅한 상태로 다음으로 진행
		int intPCode = 1;
		try {
			p_prefixCode = p_code.substring(0, 1);
			String p_surfixCode = p_code.substring(1);
			intPCode = Integer.valueOf(p_surfixCode) + 1;
		} catch (Exception e) {
		}
		// P0001 형식으로 코드 문자열 생성
		p_code = String.format("%s%04d", p_prefixCode, intPCode);
		proDTO.setP_code(p_code);
		/*
		 * 파일 리스트에 상품코드를 등록하고 상품과 파일리스트간의 연관 설정
		 */
		if (upFileList != null) {
			int nSize = upFileList.size();
			/*
			 * 파일의 개수만큼 tbl_files에 insert를 수행해야하는데 mybatis foreach를 활용한 동적쿼리를 작성하여 한번의
			 * connection으로 다수의 레코드에 insert 수행
			 */
//			for (int i = 0; i < nSize; i++) {
//				upFileList.get(i).setFile_p_code(p_code);
//				log.debug("파일정보 : " + upFileList.get(i).toString());
//
//				// 파일정보를 한개씩 DBMS에 insert 수행하기
//				// fileDao.fileInsert(upFileList.get(i));
//			}
			// fileDao.fileList(upFileList);
			fileDao.filesInsert(upFileList, p_code);

		}
		// 상품테이블의 상품정보 추가
		int ret = proDao.insert(proDTO);
		return ret;
	}

	public int update(ProductDTO proDTO) {

		return proDao.update(proDTO);
	}

	public int update(ProductDTO proDTO, List<ProFileDTO> upFileList) {
		if (upFileList != null) {
			String p_code = proDTO.getP_code();
			fileDao.filesInsert(upFileList, p_code);
		}

		return proDao.update(proDTO);
	}

	/*
	 * sub file 삭제 tbl_files에서 file_seq에 해당하는 이미지 파일 정보를 가져오기 1. 이미지 파일을 삭제하고 2. 이미지
	 * 파일이 삭제가 완료 되면 table에서 파일 정보 제거
	 */
	public String subImgDelete(String file_seq) {
		ProFileDTO proFileDTO = fileDao.findByFileSeq(file_seq);
		String file_name = proFileDTO.getFile_upload_name();
		String p_code = proFileDTO.getFile_p_code();
		
		fService.fileDelete(file_name);
		fileDao.fileDelete(file_seq);
		
		return p_code;
	}

	// 상품코드를 매개변수로 받아서
	// 대표이미지와 이미지 정보를 제거
	public void proImgDelete(String p_code) {

		ProductDTO proDTO = proDao.findByPCode(p_code);
		if (proDTO.getP_file() != null && !proDTO.getP_file().isEmpty()) {

			fService.fileDelete(proDTO.getP_file());
			proDTO.setP_file(null);
			proDao.update(proDTO);
		}

	}

}
